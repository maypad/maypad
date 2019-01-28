package de.fraunhofer.iosb.maypadbackend.services.reporefresh;

import de.fraunhofer.iosb.maypadbackend.config.project.ProjectConfig;
import de.fraunhofer.iosb.maypadbackend.config.project.data.BranchProperty;
import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.WebhookBuild;
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
import de.fraunhofer.iosb.maypadbackend.util.FileUtil;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


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
    private Set<Integer> lockedProjects; //boolean: allows init while locked
    private Logger logger = LoggerFactory.getLogger(RepoService.class);


    /**
     * Constructor for the RepoService.
     *
     * @param projectService   Projectservice
     * @param serverConfig     Configuration for server
     * @param branchRepository Database-Repository for branches
     */
    @Autowired
    public RepoService(ProjectService projectService, ServerConfig serverConfig, BranchRepository branchRepository) {
        this.projectService = projectService;
        this.serverConfig = serverConfig;
        this.lockedProjects = ConcurrentHashMap.newKeySet();
        this.branchRepository = branchRepository;
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
    @Async
    public void refreshProject(int id) {
        Project project = projectService.getProject(id);

        if (repoLock(project)) {
            logger.info("Project with id " + project.getId() + " is currently updated");
            return;
        }
        try {
            doRefreshProject(project);
        } finally {
            KeyFileManager.deleteSshFile(projectService.getRepoDir(id), id);
            removeLock(project);
        }
    }

    /**
     * Init first time the given project.
     *
     * @param id Project-id for the local repository
     */
    @Async
    public void initProject(int id) {
        Project project = projectService.getProject(id);
        if (repoLock(project)) {
            logger.info("Project with id " + project.getId() + " is currently initializing");
            return;
        }
        try {
            doInitProject(project);
        } finally {
            KeyFileManager.deleteSshFile(projectService.getRepoDir(id), id);
            removeLock(project);
        }
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
                project.setRepositoryStatus(Status.FAILED);
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

        List<String> branchNamesRepo = repoManager.getBranchNames();
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
                    branch.setDependencies(new LinkedHashSet<>());
                    branch.setBuildType(null);
                    branch.setDescription("");
                    project.getRepository().getBranches().put(branchname, branchRepository.saveAndFlush(branch));
                } else {
                    //update existing branch. Now it is a maypad_all branch
                    removeProjectConfigDataInBranch(branch);
                    branchRepository.saveAndFlush(branch);
                }
            } else {
                //branch is a "real" maypad branch
                Branch branch = project.getRepository().getBranches().get(branchname);
                if (branch == null) {
                    //new branch, so create it
                    logger.info("Create new branch " + branchname + " for project with id " + project.getId());
                    Branch tempBranch = buildBranchModelFromConfig(branchConfigData.get(branchname));
                    tempBranch.setBuildStatus(Status.UNKNOWN);
                    branch = branchRepository.saveAndFlush(tempBranch);
                    project.getRepository().getBranches().put(branchname, branch);
                } else {
                    //branch already exists. So compare Branch and update
                    if (hasMaypadConfigChanged) {
                        branch.compareAndUpdate(buildBranchModelFromConfig(branchConfigData.get(branchname)));
                    }
                    branchRepository.saveAndFlush(branch);
                }
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
            branchRepository.delete(project.getRepository().getBranches().get(branchname));
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
        //TODO


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
            project.setRepositoryStatus(Status.FAILED);
            return;
        }
        File file = new File(parentDir.getAbsolutePath() + File.separator + project.getId());
        if (file.isDirectory() && file.exists()) {
            logger.warn("Folder already exists at " + file.getAbsolutePath());
        } else if (!file.mkdirs()) {
            logger.error("Can't create directories for " + file.getAbsolutePath());
            initNullRepository(repository);
            project.setRepositoryStatus(Status.FAILED);
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

        boolean cloneSuccess = repoManager.cloneRepository();
        if (!cloneSuccess) {
            project.setRepositoryStatus(Status.FAILED);
        } else {
            project.setRepositoryStatus(Status.INIT);
        }

        project = projectService.saveProject(project);
        //if repo isn't null, so we can refresh the data. Preventing call this method twice.
        if (repositoryType != RepositoryType.NONE && cloneSuccess) {
            doRefreshProject(project);
        }
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
        for (Branch branch : project.getRepository().getBranches().values()) {
            branchRepository.deleteById(branch.getId());
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
            branch.setDeploymentType(new WebhookDeployment(new ExternalWebhook(branchProperty.getDeployment().getUrl()),
                    branchProperty.getDeployment().getDeploymentName()));
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

}
