package de.fraunhofer.iosb.maypadbackend.services;

import de.fraunhofer.iosb.maypadbackend.config.server.ServerConfig;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ServiceAccountRequest;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.repository.RepositoryType;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.KeyServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.ServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.UserServiceAccount;
import de.fraunhofer.iosb.maypadbackend.repositories.ProjectRepository;
import de.fraunhofer.iosb.maypadbackend.services.scheduler.SchedulerService;
import de.fraunhofer.iosb.maypadbackend.services.sse.EventData;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseEventType;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseMessages;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseService;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Service managing projects of a {@link Projectgroup}.
 *
 * @version 1.0
 */
@Service
@NoArgsConstructor
public class ProjectService {

    private WebhookService webhookService;
    private ProjectgroupService projectgroupService;
    private SseService sseService;
    private SchedulerService schedulerService;
    private ProjectRepository projectRepository;
    private ServerConfig serverConfig;

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    /**
     * Constructor for ProjectService.
     *
     * @param webhookService      Service for webhooks
     * @param projectgroupService Service for projectgroups
     * @param schedulerService    Service for scheduled tasks
     * @param projectRepository   Repository for database access
     * @param serverConfig        Serverconfiguration
     */
    @Autowired
    @Lazy
    public ProjectService(WebhookService webhookService, ProjectgroupService projectgroupService, SseService sseService,
                          SchedulerService schedulerService, ProjectRepository projectRepository, ServerConfig serverConfig) {
        this.webhookService = webhookService;
        this.projectgroupService = projectgroupService;
        this.sseService = sseService;
        this.schedulerService = schedulerService;
        this.projectRepository = projectRepository;
        this.serverConfig = serverConfig;
    }

    /**
     * Create a project.
     *
     * @param projectgroupId Id of the project group to which the project belongs
     * @param repositoryUrl  Url to the repository
     * @return Created project
     */
    public Project create(int projectgroupId, String repositoryUrl, String versionControlSystem) {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setGroupId(projectgroupId);
        request.setRepositoryUrl(repositoryUrl);
        request.setVersionControlSystem(versionControlSystem);
        return create(request);
    }

    /**
     * Create a project.
     *
     * @param request Request with the needed data
     * @return Created project
     */
    public Project create(CreateProjectRequest request) {
        String repoTypeString = request.getVersionControlSystem();
        RepositoryType type = null;
        for (RepositoryType t : RepositoryType.values()) {
            if (t.toString().toLowerCase().equals(repoTypeString.toLowerCase())) {
                type = t;
                break;
            }
        }
        if (type == null) {
            logger.error("Couldn't find repository type " + repoTypeString);
            type = RepositoryType.NONE;
        } else if (request.getRepositoryUrl() == null) {
            logger.error("Repository-URL is null");
            type = RepositoryType.NONE;
        }
        ServiceAccount serviceAccount = getServiceAccount(request.getServiceAccount());
        Project project = new Project(request.getRepositoryUrl(), serviceAccount);
        //we have to get the projectid, so save it first
        project = saveProject(project);
        project.setRefreshWebhook(webhookService.generateRefreshWebhook(project.getId()));
        project.getRepository().setRepositoryType(type);
        project = saveProject(project);
        addProjectToProjectgroup(request.getGroupId(), project);
        schedulerService.scheduleRepoRefresh(project.getId());
        return project;
    }

    /**
     * Get all Branches of a project.
     *
     * @param id Id of the project
     * @return List with all branches from given project(id)
     */
    public List<Branch> getBranches(int id) {
        if (getProject(id).getRepository() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(getProject(id).getRepository().getBranches().values());
    }

    /**
     * Get all projects.
     *
     * @return List with all projects
     */
    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    /**
     * Get a project by its id.
     *
     * @param id Id of project
     * @return Project with given Id
     */
    public Project getProject(int id) {
        return (projectRepository.findById(id).orElseThrow(projectNotFoundException(id)));
    }

    /**
     * Get a Branch in a project with its name.
     *
     * @param id  Id of the project
     * @param ref Name of the branch
     * @return Branch with given name, null if no branch (with this name) exists
     */
    public Branch getBranch(int id, String ref) {
        return getProject(id).getRepository().getBranches().get(ref);
    }

    /**
     * Get the directory for the repo files for a project.
     *
     * @param project The project
     * @return The directory for the repo files
     */
    public File getRepoDir(Project project) {
        if (project == null) {
            return null;
        }
        return getRepoDir(project.getId());
    }

    /**
     * Get the directory for the repo files for a project with the id.
     *
     * @param id The id of a project
     * @return The directory for the repo files
     */
    public File getRepoDir(int id) {
        return new File(serverConfig.getRepositoryStoragePath() + File.separator + id);
    }

    /**
     * Change the data of a project.
     *
     * @param id      Id of the project
     * @param request Request with the data
     * @return Updated project
     */
    public Project changeProject(int id, ChangeProjectRequest request) {
        Project project = getProject(id);
        ServiceAccount serviceAccount = getServiceAccount(request.getServiceAccount());
        EventData sseEvent = EventData.builder(SseEventType.PROJECT_CHANGED)
                .projectId(id)
                .message(SseMessages.SERVICEACCOUNT_CHANGED)
                .build();
        sseService.push(sseEvent);
        project.setServiceAccount(serviceAccount);
        return saveProject(project);
    }

    /**
     * Delete a project.
     *
     * @param id Id of the project
     */
    public void deleteProject(int id) {
        //check if repo id is valid and remove webhook
        webhookService.removeWebhook(getProject(id).getRefreshWebhook());
        getProject(id);
        schedulerService.unscheduleRepoRefresh(id);
        projectRepository.deleteById(id);
    }

    /**
     * Save a project.
     *
     * @param project Specific project
     * @return Saved project
     */
    public Project saveProject(Project project) {
        return projectRepository.saveAndFlush(project);
    }

    /**
     * Create a new Serviceaccount.
     *
     * @param request Request with data for a serviceaccount
     * @return Serviceaccount if the needed data were available, else null
     */
    private ServiceAccount getServiceAccount(ServiceAccountRequest request) {
        if (request == null) {
            return null;
        }
        ServiceAccount serviceAccount = null;
        if (request.getSshKey() != null && request.getSshKey().isPresent()) {
            serviceAccount = new KeyServiceAccount(request.getSshKey().get());
        } else if (request.getUsername() != null && request.getPassword() != null
                && request.getUsername().isPresent() && request.getPassword().isPresent()) {
            serviceAccount = new UserServiceAccount(request.getUsername().get(),
                    request.getPassword().get());
        }
        return serviceAccount;
    }

    /**
     * Add a project to a projectgroup.
     *
     * @param projectgroupId Id of the projectgroup
     * @param project        Specific project
     */
    private void addProjectToProjectgroup(int projectgroupId, Project project) {
        if (project == null) {
            return;
        }

        Projectgroup projectgroup = projectgroupService.getProjectgroup(projectgroupId);
        projectgroup.getProjects().add(project);
        projectgroupService.saveProjectgroup(projectgroup);
    }

    /**
     * Tells the projectgroup owning the project with the given id to update its status.
     */
    public void statusPropagation(int id) {
        Projectgroup projectgroup = projectgroupService.getProjectrgroupByProject(getProject(id));
        projectgroupService.updateStatus(projectgroup.getId());
    }

    /**
     * Throw an exception if the project was not found.
     *
     * @param id Id of the project
     * @return Represents a supplier for this exception
     */
    private Supplier<NotFoundException> projectNotFoundException(int id) {
        return () -> new NotFoundException("PROJECT_NOT_FOUND",
                String.format("Project with id %d not found!", id));
    }

}
