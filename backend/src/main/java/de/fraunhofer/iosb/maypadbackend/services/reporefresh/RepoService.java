package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.data.BranchProperty;
import de.fraunhofer.iosb.maypadbackend.config.project.data.BuildProperty;
import de.fraunhofer.iosb.maypadbackend.config.project.data.DeploymentProperty;
import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.WebhookBuild;
import de.fraunhofer.iosb.maypadbackend.model.deployment.WebhookDeployment;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.Commit;
import de.fraunhofer.iosb.maypadbackend.model.repository.DependencyDescriptor;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.repository.RepositoryType;
import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.services.sse.EventData;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseEventType;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseService;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import de.fraunhofer.iosb.maypadbackend.util.FileUtil;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import de.fraunhofer.iosb.maypadbackend.util.datastructures.ExpiredElementRemover;
import de.fraunhofer.iosb.maypadbackend.util.datastructures.ExpiringElement;
import de.fraunhofer.iosb.maypadbackend.util.datastructures.Util;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


/**
 * RepoService, managing all operations with repositories.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Service
@NoArgsConstructor
public class RepoService {

    private ProjectService projectService;
    private ServerConfig serverConfig;
    private WebhookService webhookService;
    private SseService sseService;
    private Map<Integer, Semaphore> lockedProjects;
    private static final Logger logger = LoggerFactory.getLogger(RepoService.class);
    private List<ExpiringElement> refreshCounter;

    /**
     * Constructor for the RepoService.
     *
     * @param projectService Projectservice
     * @param serverConfig   Configuration for server
     * @param webhookService Webhookservice
     * @param sseService     Service for server-sent events
     */
    @Autowired
    public RepoService(ProjectService projectService, ServerConfig serverConfig,
                       WebhookService webhookService, SseService sseService) {
        this.projectService = projectService;
        this.serverConfig = serverConfig;
        this.lockedProjects = new ConcurrentHashMap<>();
        this.webhookService = webhookService;
        this.sseService = sseService;
        if (serverConfig.isMaximumRefreshRequestsEnabled()) {
            refreshCounter = Collections.synchronizedList(new ArrayList<>());
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(new ExpiredElementRemover(refreshCounter), 0, 15, TimeUnit.SECONDS);
        }
    }

    /**
     * Refresh and update the given project.
     *
     * @param id Project-id to update
     */
    @Async("repoRefreshPool")
    public Future<Void> refreshProject(int id) {
        Project project = projectService.getProject(id);

        if (!repoLock(id)) {
            logger.info("Project with id " + project.getId() + " is currently updated");
            return CompletableFuture.completedFuture(null);
        }
        //check if max refresh amount is reached
        if (isRefreshCapReached(id)) {
            removeLock(id);
            return CompletableFuture.completedFuture(null);
        }
        try {
            doRefreshProject(project);
        } finally {
            KeyFileManager.deleteSshFile(new File(serverConfig.getRepositoryStoragePath()), id);
            removeLock(id);
            project = projectService.getProject(id);
            sseService.push(EventData.builder(SseEventType.PROJECT_REFRESHED).projectId(id).name(project.getName()).build());
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Init first time the given project.
     *
     * @param id Project-id for the local repository
     */
    @Async("repoRefreshPool")
    public Future<Void> initProject(int id) {
        Project project = projectService.getProject(id);
        if (!repoLock(id)) {
            logger.info("Project with id " + project.getId() + " is currently initializing");
            return CompletableFuture.completedFuture(null);
        }
        //check if max refresh amount is reached
        if (isRefreshCapReached(id)) {
            setStatusAndSave(project, Status.ERROR);
            removeLock(id);
            return CompletableFuture.completedFuture(null);
        }
        try {
            doInitProject(project);
        } finally {
            KeyFileManager.deleteSshFile(new File(serverConfig.getRepositoryStoragePath()), id);
            project = projectService.getProject(id);
            sseService.push(EventData.builder(SseEventType.PROJECT_INIT).projectId(id).name(project.getName()).build());
            removeLock(id);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Delete whole project.
     *
     * @param id id of the project
     */
    @Async
    public Future<Void> deleteProject(int id) {
        boolean weHaveTheLock = false;
        while (!weHaveTheLock) {
            if (repoLock(id)) {
                weHaveTheLock = true;
                continue;
            }
            //repo is locked, so an semaphore exists normally
            Semaphore semaphore = lockedProjects.get(id);
            if (semaphore != null) {
                logger.info("Project with id " + id + " is currently locked. So wait, until other tasks have been finished.");
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    logger.error("Error deleting project with id " + id);
                    return CompletableFuture.completedFuture(null);
                }
            }
        }
        logger.info("Started delete of project with id " + id);

        Project project;
        try {
            project = projectService.getProject(id);
        } catch (NotFoundException e) {
            //project does not exist.
            return CompletableFuture.completedFuture(null);
        }
        if (project == null) {
            //some error with the project occurred
            return CompletableFuture.completedFuture(null);
        }

        if (project.getRepository().getBranches() != null) {
            for (Branch branch : project.getRepository().getBranches().values()) {
                removeAllWebhooks(branch);
            }
        }

        projectService.deleteProject(project.getId());
        removeLock(id);
        FileUtil.deleteAllFiles(projectService.getRepoDir(project.getId()));
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Lock a repository, so the same repo cannot updated twice at the same time.
     *
     * @param projectid Projectid which should be locked
     * @return true, if lock was successfully, else false (so another project is locked currently)
     */
    private synchronized boolean repoLock(int projectid) {
        if (lockedProjects.containsKey(projectid)) {
            return false;
        }
        Semaphore semaphore = new Semaphore(1);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lockedProjects.put(projectid, semaphore);
        return true;
    }

    /**
     * Remove a lock of a project.
     *
     * @param projectid Projectid which should be unlocked
     */
    private synchronized void removeLock(int projectid) {
        lockedProjects.get(projectid).release();
        lockedProjects.remove(projectid);
    }

    /**
     * Check if a update and refresh cap for all projects is reached in a time.
     *
     * @param id id of the project
     * @return true, if the cap is already reached, else false
     */
    private boolean isRefreshCapReached(int id) {
        if (serverConfig.isMaximumRefreshRequestsEnabled()) {
            if (refreshCounter.size() >= serverConfig.getMaximumRefreshRequests()) {
                logger.warn("Cap limit of " + serverConfig.getMaximumRefreshRequests() + " for max refresh requests "
                        + "was reached. Skip refresh of project with id " + id);
                return true;

            }
            refreshCounter.add(new ExpiringElement(serverConfig.getMaximumRefreshRequestsSeconds(), TimeUnit.SECONDS));
        }
        return false;
    }

    /**
     * Refresh a project.
     *
     * @param project Project to refresh
     */
    private void doRefreshProject(Project project) {
        if (project.getRepository() == null || project.getRepository().getRepositoryType() == RepositoryType.NONE
                || project.getRepositoryStatus() == Status.ERROR) {
            doInitProject(project);
            //lock is removed in init
            return;
        }
        logger.info("Start refresh project with id " + project.getId());

        RepoManager repoManager = project.getRepository().getRepositoryType().toRepoManager(project);
        repoManager.initRepoManager(new File(serverConfig.getRepositoryStoragePath()), projectService.getRepoDir(project.getId()));
        repoManager.prepareRefresh();

        //clone project, if data were deleted
        File repoDir = projectService.getRepoDir(project.getId());
        if (!repoDir.exists()) {
            if (!repoDir.mkdirs() || !repoManager.cloneRepository()) {
                setStatusAndSave(project, Status.ERROR);
                repoManager.cleanUp();
                return;
            }
        }

        repoManager.switchBranch(repoManager.getMainBranchName());

        //compare Maypad config hash
        Tuple<ProjectConfig, File> projectConfigData = repoManager.getProjectConfig();
        if (projectConfigData == null) {
            logger.error("Error with maypad configuration in project with id " + project.getId());
            setStatusAndSave(project, Status.FAILED);
            repoManager.cleanUp();
            return;
        }

        String hash = FileUtil.calcSha256(projectConfigData.getValue());
        if (hash == null) {
            logger.error("Can't read maypad config with projectid " + project.getId());
            setStatusAndSave(project, Status.FAILED);
            repoManager.cleanUp();
            return;
        }

        boolean hasMaypadConfigChanged = !hash.equals(project.getRepository().getMaypadConfigHash());
        if (hasMaypadConfigChanged) {
            //config has changed or didn't exists before
            logger.info("Maypad-Config for project with id " + project.getId() + " has changed");
        }

        //update project
        project.setName(projectConfigData.getKey().getProjectName());
        project.setDescription(projectConfigData.getKey().getProjectDescription());
        project.setRepoWebsiteUrl(projectConfigData.getKey().getRepoWebsiteUrl());
        project.getRepository().setMaypadConfigHash(hash);

        //update branches
        boolean allBranches = projectConfigData.getKey().getAddAllBranches();
        Map<String, BranchProperty> branchConfigData = new HashMap<>();
        for (BranchProperty branchProperty : projectConfigData.getKey().getBranchProperties()) {
            branchConfigData.put(branchProperty.getName(), branchProperty);
        }

        List<String> deleteBranches = new LinkedList<>();
        List<String> branchNamesRepo = repoManager.getBranchNames();
        List<String> generateWebhooks = new LinkedList<>();

        for (String branchname : branchNamesRepo) {
            if (!branchConfigData.containsKey(branchname)) {
                if (!allBranches) {
                    //we haven't to check this branch cause it isn't needed. But remove it, if it exists
                    if (project.getRepository().getBranches().get(branchname) != null) {
                        deleteBranches.add(branchname);
                    }
                    continue;
                }
                //do detailed information, so we have only the branchname
                Branch branch = project.getRepository().getBranches().get(branchname);
                if (branch == null) {
                    //Branch was new created in repo. But only as maypad_all
                    logger.info("Create new maypad_all branch " + branchname + " for project with id " + project.getId());
                    branch = new Branch();
                    branch.setName(branchname);
                    branch.setBuildStatus(Status.UNKNOWN);
                    branch.setDependencies(new ArrayList<>());
                    branch.setBuildType(null);
                    branch.setDescription("");
                    generateWebhooks.add(branchname);
                    project.getRepository().getBranches().put(branchname, branch);
                } else {
                    //update existing branch. Now it is a maypad_all branch
                    removeProjectConfigDataInBranch(branch);
                }
            } else {
                //branch is a "real" maypad branch
                Branch branch = project.getRepository().getBranches().get(branchname);
                if (branch == null) {
                    //new branch, so create it
                    logger.info("Create new branch " + branchname + " for project with id " + project.getId());
                    Branch tempBranch = buildBranchModelFromConfig(branchConfigData.get(branchname));
                    tempBranch.setBuildStatus(Status.UNKNOWN);
                    generateWebhooks.add(branchname);
                    project.getRepository().getBranches().put(branchname, tempBranch);
                } else {
                    //branch already exists. So compare Branch and update
                    if (hasMaypadConfigChanged) {
                        branch.compareAndUpdate(buildBranchModelFromConfig(branchConfigData.get(branchname)));
                    }
                }
            }

        }
        project = projectService.saveProject(project);
        for (String webhookBranchname : generateWebhooks) {
            //generate webhooks
            generateAllNeededWebhooks(project.getId(), project.getRepository().getBranches().get(webhookBranchname));
        }

        //remove not existing branches
        for (String branchname : project.getRepository().getBranches().keySet()) {
            if (!branchNamesRepo.contains(branchname)) {
                deleteBranches.add(branchname);
            }
        }
        for (String branchname : deleteBranches) {
            logger.info("Remove branch " + branchname + " in project with id " + project.getId());
            Branch branch = project.getRepository().getBranches().get(branchname);
            removeAllWebhooks(branch);
            project.getRepository().getBranches().remove(branchname);
        }

        Commit lastProjectCommit = null;

        //update repo data from projects
        for (Branch branch : project.getRepository().getBranches().values()) {
            if (!repoManager.switchBranch(branch.getName())) {
                logger.info("Can't switch to branch " + branch.getName());
                continue;
            }

            //last branch commit
            Commit lastCommit = repoManager.getLastCommit();
            if (branch.getLastCommit() == null) {
                branch.setLastCommit(lastCommit);
            } else {
                branch.getLastCommit().compareAndUpdate(lastCommit);
            }

            if (lastProjectCommit == null || lastCommit.getTimestamp().getTime() < lastCommit.getTimestamp().getTime()) {
                lastProjectCommit = lastCommit;
            }

            //readme
            String readme = repoManager.getReadme();
            if (!readme.equals(branch.getReadme())) {
                branch.setReadme(readme);
            }
        }

        //Project last commit
        if (project.getRepository().getLastCommit() == null) {
            project.getRepository().setLastCommit(lastProjectCommit);
        } else {
            project.getRepository().getLastCommit().compareAndUpdate(lastProjectCommit);
        }


        //tags
        if (project.getRepository().getTags() == null) {
            project.getRepository().setTags(repoManager.getTags());
        } else {
            Util.updateList(project.getRepository().getTags(), repoManager.getTags());
        }


        project.setLastUpdate(new Date());
        project.setRepositoryStatus(Status.SUCCESS);

        projectService.saveProject(project);
        logger.info("Project with id " + project.getId() + " has refreshed.");
    }

    /**
     * Init a new project.
     *
     * @param project Project to init
     */
    private void doInitProject(Project project) {
        logger.info("Start init project with id " + project.getId());
        Repository repository = project.getRepository();
        //repo wasn't created before
        if (repository == null) {
            repository = new Repository();
            repository.setBranches(new ConcurrentHashMap<>());
            repository.setTags(new ArrayList<>());
            project.setRepository(repository);
        }

        File parentDir = new File(serverConfig.getRepositoryStoragePath());

        if (!FileUtil.hasWriteAccess(parentDir)) {
            logger.error("Can't read / write to (maybe missing) " + parentDir.getAbsolutePath());
            initNullRepository(repository);
            setStatusAndSave(project, Status.ERROR);
            return;
        }
        File file = new File(parentDir.getAbsolutePath() + File.separator + project.getId());
        if (file.isDirectory() && file.exists()) {
            logger.warn("Folder already exists at " + file.getAbsolutePath());
        } else if (!file.mkdirs()) {
            logger.error("Can't create directories for " + file.getAbsolutePath());
            initNullRepository(repository);
            setStatusAndSave(project, Status.ERROR);
            return;
        }
        RepositoryType repositoryType = project.getRepository().getRepositoryType();

        //clone
        RepoManager repoManager = repositoryType.toRepoManager(project);
        repoManager.initRepoManager(parentDir, projectService.getRepoDir(project.getId()));

        boolean cloneSuccess = (repoManager.cloneRepository() && repositoryType != RepositoryType.NONE);
        if (!cloneSuccess) {
            setStatusAndSave(project, Status.ERROR);
            repoManager.cleanUp();
            return;
        }

        project.setRepositoryStatus(Status.INIT);


        project = projectService.saveProject(project);
        //if repo isn't null, so we can refresh the data. Preventing call this method twice.
        doRefreshProject(project);
    }

    /**
     * Set the repository to a null repository.
     *
     * @param repository Repository to change
     */
    private void initNullRepository(Repository repository) {
        if (repository == null || repository.getRepositoryType() == RepositoryType.NONE) {
            return;
        }
        repository.setRepositoryType(RepositoryType.NONE);
    }

    /**
     * Change the repository-status of a project.
     *
     * @param project Project to change
     * @param status  New status for the project
     * @return The updated project
     */
    private Project setStatusAndSave(Project project, Status status) {
        if (project == null) {
            return null;
        }
        project.setRepositoryStatus(status);
        return projectService.saveProject(project);
    }

    /**
     * Reset a branch, so remove all data which were in the project config.
     *
     * @param branch Branch to remove the project data
     */
    private void removeProjectConfigDataInBranch(Branch branch) {
        branch.setDescription("");
        branch.setMembers(null);
        branch.setMails(null);
        branch.setBuildType(null);
        branch.setDeploymentType(null);
        branch.setDependencies(new ArrayList<>());
    }

    /**
     * Build a branch with given settings.
     *
     * @param branchProperty Branch settings
     * @return The built branch
     */
    private Branch buildBranchModelFromConfig(BranchProperty branchProperty) {
        Branch branch = new Branch();
        branch.setName(branchProperty.getName());
        branch.setDescription(branchProperty.getDescription() != null ? branchProperty.getDescription() : "");

        //members
        List<Person> members = new ArrayList<>();
        if (branchProperty.getMembers() != null) {
            for (String member : branchProperty.getMembers()) {
                members.add(new Person(member));
            }
        }
        branch.setMembers(members);

        //mails
        List<Mail> mails = new ArrayList<>();
        if (branchProperty.getMails() != null) {
            for (String mail : branchProperty.getMails()) {
                mails.add(new Mail(mail));
            }
        }
        branch.setMails(mails);

        //build
        BuildProperty buildProperty = branchProperty.getBuild();
        if (buildProperty != null && buildProperty.getType() != null) {
            switch (buildProperty.getType().toLowerCase()) {
                case "webhook":
                    if (buildProperty.getUrl() != null) {
                        HttpMethod method = buildProperty.getMethod() == null ? HttpMethod.POST : buildProperty.getMethod();
                        String body = buildProperty.getBody() == null ? "{}" : buildProperty.getBody();
                        HttpHeaders headers = new HttpHeaders();
                        if (buildProperty.getHeaders() != null) {
                            Arrays.stream(buildProperty.getHeaders())
                                    .forEach(h -> headers.put(h.getKey(), Arrays.asList(h.getValues())));
                        }
                        branch.setBuildType(new WebhookBuild(new ExternalWebhook(buildProperty.getUrl()), method, headers, body));
                        break;
                    }
                    logger.warn(String.format("Missing parameter for BuildType %s.", buildProperty.getType()));
                    break;
                default:
                    logger.warn(String.format("Unknown BuildType %s.", buildProperty.getType()));
            }
        }

        //deployment
        DeploymentProperty deploymentProperty = branchProperty.getDeployment();
        if (deploymentProperty != null) {
            switch (deploymentProperty.getType().toLowerCase()) {
                case "webhook":
                    if (deploymentProperty.getUrl() != null) {
                        HttpMethod method = deploymentProperty.getMethod() == null ? HttpMethod.POST : deploymentProperty.getMethod();
                        String body = deploymentProperty.getBody() == null ? "{}" : deploymentProperty.getBody();
                        HttpHeaders headers = new HttpHeaders();
                        if (deploymentProperty.getHeaders() != null) {
                            Arrays.stream(deploymentProperty.getHeaders())
                                    .forEach(h -> headers.put(h.getKey(), Arrays.asList(h.getValues())));
                        }
                        branch.setDeploymentType(new WebhookDeployment(deploymentProperty.getName(),
                                new ExternalWebhook(deploymentProperty.getUrl()), method, headers, body));
                        break;
                    }
                    logger.warn(String.format("Missing parameter for DeploymentType %s.", deploymentProperty.getType()));
                    break;
                default:
                    logger.warn("Unknown deploymentType " + branchProperty.getDeployment().getType() + " in branch " + branch.getName());
                    break;
            }
        }

        //dependencies
        List<DependencyDescriptor> dependencies = new ArrayList<>();
        if (branchProperty.getDependsOn() != null) {
            for (String dependOn : branchProperty.getDependsOn()) {
                String[] dependParts = dependOn.split(":");
                if (dependParts.length != 2) {
                    logger.warn("Invalid dependency " + dependOn + " on branch " + branch.getName());
                    continue;
                }
                int projectid;
                try {
                    projectid = Integer.parseInt(dependParts[0]);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid projectnumber " + dependOn + " on branch " + branch.getName());
                    continue;
                }
                dependencies.add(new DependencyDescriptor(projectid, dependParts[1]));
            }
        }

        branch.setDependencies(dependencies);

        return branch;

    }

    private void generateAllNeededWebhooks(int projectid, Branch branch) {
        if (branch == null) {
            return;
        }
        logger.info("Generate webhooks for branch " + branch.getName() + " in project with id " + projectid);
        branch.setBuildSuccessWebhook(webhookService.generateSuccessWebhook(new Tuple<>(projectid, branch.getName())));
        branch.setBuildFailureWebhook(webhookService.generateFailWebhook(new Tuple<>(projectid, branch.getName())));
    }

    /**
     * Remove all webhooks from a branch.
     *
     * @param branch Branch to remove webhooks
     */
    private void removeAllWebhooks(Branch branch) {
        if (branch == null) {
            return;
        }
        logger.info("Delete all webhooks in branch " + branch.getName());
        webhookService.removeWebhook(branch.getBuildSuccessWebhook());
        webhookService.removeWebhook(branch.getBuildFailureWebhook());
    }
}
