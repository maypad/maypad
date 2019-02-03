package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.data.BranchProperty;
import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.WebhookBuild;
import de.fraunhofer.iosb.maypadbackend.model.deployment.ScriptDeployment;
import de.fraunhofer.iosb.maypadbackend.model.deployment.WebhookDeployment;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.DependencyDescriptor;
import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.repository.RepositoryType;
import de.fraunhofer.iosb.maypadbackend.model.webhook.ExternalWebhook;
import de.fraunhofer.iosb.maypadbackend.repositories.BranchRepository;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import de.fraunhofer.iosb.maypadbackend.util.FileUtil;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import de.fraunhofer.iosb.maypadbackend.util.datastructures.ExpiredKeyRemover;
import de.fraunhofer.iosb.maypadbackend.util.datastructures.ExpiringElement;
import de.fraunhofer.iosb.maypadbackend.util.datastructures.Util;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * RepoService, managing all operations with repositories.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Service
@NoArgsConstructor
@EnableAsync
public class RepoService {

    private ProjectService projectService;
    private ServerConfig serverConfig;
    private BranchRepository branchRepository;
    private WebhookService webhookService;
    private Set<Integer> lockedProjects; //boolean: allows init while locked
    private Logger logger = LoggerFactory.getLogger(RepoService.class);
    private List<de.fraunhofer.iosb.maypadbackend.util.datastructures.ExpiringElement> refreshCounter;

    /**
     * Constructor for the RepoService.
     *
     * @param projectService   Projectservice
     * @param serverConfig     Configuration for server
     * @param branchRepository Database-Repository for branches
     * @param webhookService   Webhookservice
     */
    @Autowired
    public RepoService(ProjectService projectService, ServerConfig serverConfig, BranchRepository branchRepository,
                       WebhookService webhookService) {
        this.projectService = projectService;
        this.serverConfig = serverConfig;
        this.lockedProjects = ConcurrentHashMap.newKeySet();
        this.branchRepository = branchRepository;
        this.webhookService = webhookService;
        if (serverConfig.isMaximumRefreshRequestsEnabled()) {
            refreshCounter = Collections.synchronizedList(new ArrayList<>());
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(new ExpiredKeyRemover(refreshCounter), 0, 15, TimeUnit.SECONDS);
        }

    }

    private synchronized boolean repoLock(Project project) {
        if (lockedProjects.contains(project.getId())) {
            return true;
        }
        lockedProjects.add(project.getId());
        return false;
    }

    private synchronized void removeLock(Project project) {
        lockedProjects.remove(project.getId());
    }

    /**
     * Refresh and update the given project.
     *
     * @param id Project-id to update
     */
    @Async("repoRefreshPool")
    public void refreshProject(int id) {
        Project project = projectService.getProject(id);

        if (repoLock(project)) {
            logger.info("Project with id " + project.getId() + " is currently updated");
            return;
        }
        //check if max refresh amount is reached
        if (isRefreshCapReached(id)) {
            removeLock(project);
            return;
        }
        try {
            doRefreshProject(project);
        } finally {
            KeyFileManager.deleteSshFile(new File(serverConfig.getRepositoryStoragePath()), id);
            removeLock(project);
        }
    }

    /**
     * Init first time the given project.
     *
     * @param id Project-id for the local repository
     */
    @Async("repoRefreshPool")
    public void initProject(int id) {
        Project project = projectService.getProject(id);
        if (repoLock(project)) {
            logger.info("Project with id " + project.getId() + " is currently initializing");
            return;
        }
        //check if max refresh amount is reached
        if (isRefreshCapReached(id)) {
            setFailStatusAndSave(project);
            removeLock(project);
            return;
        }
        try {
            doInitProject(project);
        } finally {
            KeyFileManager.deleteSshFile(new File(serverConfig.getRepositoryStoragePath()), id);
            removeLock(project);
        }
    }

    private boolean isRefreshCapReached(int id) {
        if (serverConfig.isMaximumRefreshRequestsEnabled()) {
            if (refreshCounter.size() >= serverConfig.getMaximumRefreshRequests()) {
                logger.warn("Cap limit of " + serverConfig.getMaximumRefreshRequests() + " for max refresh requests "
                        + "was reched. Skip refresh of project with id " + id);
                return true;

            }
            refreshCounter.add(new ExpiringElement(serverConfig.getMaximumRefreshRequestsSeconds(), TimeUnit.SECONDS));
        }
        return false;
    }

    private void doRefreshProject(Project project) {
        if (project.getRepository() == null || project.getRepository().getRepositoryType() == RepositoryType.NONE
                || project.getRepositoryStatus() == Status.FAILED) {
            doInitProject(project);
            //lock is removed in init
            return;
        }
        logger.info("Start refresh project with id " + project.getId());

        RepoManager repoManager = project.getRepository().getRepositoryType().toRepoManager(project);
        repoManager.initRepoManager(new File(serverConfig.getRepositoryStoragePath()), projectService.getRepoDir(project.getId()));
        repoManager.switchBranch(repoManager.getMainBranchName());

        //clone project, if data were deleted
        if (!projectService.getRepoDir(project.getId()).exists()) {
            if (!repoManager.cloneRepository()) {
                setFailStatusAndSave(project);
                return;
            }
        }


        //compare Maypad config hash
        Tuple<ProjectConfig, File> projectConfigData = repoManager.getProjectConfig();
        if (projectConfigData == null) {
            logger.error("No maypad configuration were found in project with id " + project.getId());
            return;
        }

        String hash = FileUtil.calcSha256(projectConfigData.getValue());
        if (hash == null) {
            logger.error("Can't read maypad config with projectid " + project.getId());
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
        project.getRepository().setMaypadConfigHash(hash);

        //update branches
        boolean allBranches = projectConfigData.getKey().getAddAllBranches();
        Map<String, BranchProperty> branchConfigData = new HashMap<>();
        for (BranchProperty branchProperty : projectConfigData.getKey().getBranchProperties()) {
            branchConfigData.put(branchProperty.getName(), branchProperty);
        }

        List<String> deleteBranches = new LinkedList<>();
        List<String> branchNamesRepo = repoManager.getBranchNames();

        for (String branchname : branchNamesRepo) {
            boolean generateWebhooks = false;
            Branch savedBranch = null;
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
                    branch.setDependencies(new LinkedHashSet<>());
                    branch.setBuildType(null);
                    branch.setDescription("");
                    savedBranch = branchRepository.saveAndFlush(branch);
                    generateWebhooks = true;
                    project.getRepository().getBranches().put(branchname, savedBranch);
                } else {
                    //update existing branch. Now it is a maypad_all branch
                    removeProjectConfigDataInBranch(branch);
                    savedBranch = branchRepository.saveAndFlush(branch);
                }
            } else {
                //branch is a "real" maypad branch
                Branch branch = project.getRepository().getBranches().get(branchname);
                if (branch == null) {
                    //new branch, so create it
                    logger.info("Create new branch " + branchname + " for project with id " + project.getId());
                    Branch tempBranch = buildBranchModelFromConfig(branchConfigData.get(branchname));
                    tempBranch.setBuildStatus(Status.UNKNOWN);
                    generateWebhooks = true;
                    savedBranch = branch = branchRepository.saveAndFlush(tempBranch);
                    project.getRepository().getBranches().put(branchname, branch);
                } else {
                    //branch already exists. So compare Branch and update
                    if (hasMaypadConfigChanged) {
                        branch.compareAndUpdate(buildBranchModelFromConfig(branchConfigData.get(branchname)));
                    }
                    savedBranch = branchRepository.saveAndFlush(branch);
                }
            }
            //generate webhooks
            if (generateWebhooks && savedBranch != null) {
                generateAllNeededWebhooks(project.getId(), savedBranch);
                savedBranch = branchRepository.saveAndFlush(savedBranch);
            }
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
            branchRepository.delete(branch);
            project.getRepository().getBranches().remove(branchname);
        }

        //update repo data from projects
        for (Branch branch : project.getRepository().getBranches().values()) {
            if (!repoManager.switchBranch(branch.getName())) {
                logger.info("Can't switch to branch " + branch.getName());
                continue;
            }
            //last branch commit
            if (branch.getLastCommit() == null) {
                branch.setLastCommit(repoManager.getLastCommit());
            } else {
                branch.getLastCommit().compareAndUpdate(repoManager.getLastCommit());
            }

            //readme
            String readme = repoManager.getReadme();
            if (!readme.equals(branch.getReadme())) {
                branch.setReadme(readme);
            }

            branchRepository.saveAndFlush(branch);
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
            logger.error("Can't read / write to " + parentDir.getAbsolutePath());
            initNullRepository(repository);
            setFailStatusAndSave(project);
            return;
        }
        File file = new File(parentDir.getAbsolutePath() + File.separator + project.getId());
        if (file.isDirectory() && file.exists()) {
            logger.warn("Folder already exists at " + file.getAbsolutePath());
        } else if (!file.mkdirs()) {
            logger.error("Can't create directories for " + file.getAbsolutePath());
            initNullRepository(repository);
            setFailStatusAndSave(project);
            return;
        }
        RepositoryType repositoryType = getCorrectRepositoryType(project.getRepositoryUrl());
        if (repositoryType == RepositoryType.NONE) {
            logger.warn("URL is missing or invalid for project with id " + project.getId());
        }
        repository.setRepositoryType(repositoryType);

        //clone
        RepoManager repoManager = repositoryType.toRepoManager(project);
        repoManager.initRepoManager(parentDir, projectService.getRepoDir(project.getId()));

        boolean cloneSuccess = (repoManager.cloneRepository() && repositoryType != RepositoryType.NONE);
        if (!cloneSuccess) {
            setFailStatusAndSave(project);
            return;
        }

        project.setRepositoryStatus(Status.INIT);


        project = projectService.saveProject(project);
        //if repo isn't null, so we can refresh the data. Preventing call this method twice.
        doRefreshProject(project);
    }

    /**
     * Delete whole project.
     *
     * @param id id of the project
     * @return true, if all data were deleted, else false
     */
    public boolean deleteProject(int id) {
        //TODO
        Project project = projectService.getProject(id);
        if (project.getRepository().getBranches() != null) {
            for (Branch branch : project.getRepository().getBranches().values()) {
                removeAllWebhooks(branch);
                branchRepository.deleteById(branch.getId());
            }
        }

        projectService.deleteProject(project.getId());
        return FileUtil.deleteAllFiles(projectService.getRepoDir(project.getId()));

    }

    private void initNullRepository(Repository repository) {
        if (repository == null || repository.getRepositoryType() == RepositoryType.NONE) {
            return;
        }
        repository.setRepositoryType(RepositoryType.NONE);
    }

    private Project setFailStatusAndSave(Project project) {
        if (project == null) {
            return null;
        }
        project.setRepositoryStatus(Status.FAILED);
        return projectService.saveProject(project);
    }

    private void removeProjectConfigDataInBranch(Branch branch) {
        branch.setDescription("");
        branch.setMembers(null);
        branch.setMails(null);
        branch.setBuildType(null);
        branch.setDeploymentType(null);
        branch.setDependencies(new LinkedHashSet<>());
    }

    private Branch buildBranchModelFromConfig(BranchProperty branchProperty) {
        Branch branch = new Branch();
        branch.setName(branchProperty.getName());
        branch.setDescription(branchProperty.getDescription() != null ? branchProperty.getDescription() : "");

        //members
        Set<Person> members = new LinkedHashSet<>();
        if (branchProperty.getMembers() != null) {
            for (String member : branchProperty.getMembers()) {
                members.add(new Person(member));
            }
        }
        branch.setMembers(members);

        //mails
        Set<Mail> mails = new LinkedHashSet<>();
        if (branchProperty.getMails() != null) {
            for (String mail : branchProperty.getMails()) {
                mails.add(new Mail(mail));
            }
        }
        branch.setMails(mails);

        //build
        if (branchProperty.getBuild() != null) {
            branch.setBuildType(new WebhookBuild(new ExternalWebhook(branchProperty.getBuild())));
        }

        //deployment
        if (branchProperty.getDeployment() != null) {
            switch (branchProperty.getDeployment().getType().toLowerCase()) {
                case "webhook":
                    branch.setDeploymentType(
                            new WebhookDeployment(new ExternalWebhook(branchProperty.getDeployment().getArguments()),
                                    branchProperty.getDeployment().getName()));
                    break;
                case "script":
                    branch.setDeploymentType(new ScriptDeployment(new File(branchProperty.getDeployment().getArguments()),
                            branchProperty.getDeployment().getName()));
                    break;
                default:
                    logger.warn("Unknown deploymenttype " + branchProperty.getDeployment().getType() + " in branch " + branch.getName());
                    break;
            }
        }

        //dependencies
        Set<DependencyDescriptor> dependencies = new LinkedHashSet<>();
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

    private RepositoryType getCorrectRepositoryType(String url) {
        if (url == null) {
            return RepositoryType.NONE;
        }
        for (RepositoryType repositoryType : RepositoryType.values()) {
            if (repositoryType.isUrlBelongToRepotype(url)) {
                return repositoryType;
            }
        }
        return RepositoryType.NONE;
    }

    private Branch generateAllNeededWebhooks(int projectid, Branch branch) {
        if (branch == null) {
            return null;
        }
        logger.info("Generate webhooks for branch " + branch.getName() + " in project with id " + projectid);
        branch.setBuildSuccessWebhook(webhookService.generateSuccessWebhook(new Tuple<>(projectid, branch.getName())));
        branch.setBuildFailureWebhook(webhookService.generateFailWebhook(new Tuple<>(projectid, branch.getName())));
        return branch;
    }

    private void removeAllWebhooks(Branch branch) {
        if (branch == null) {
            return;
        }
        logger.info("Delete all webhooks in branch " + branch.getName());
        webhookService.removeWebhook(branch.getBuildSuccessWebhook());
        webhookService.removeWebhook(branch.getBuildFailureWebhook());
    }

}
