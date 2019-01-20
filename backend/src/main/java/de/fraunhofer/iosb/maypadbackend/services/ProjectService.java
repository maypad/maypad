package de.fraunhofer.iosb.maypadbackend.services;

import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ServiceAccountRequest;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.KeyServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.ServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.UserServiceAccount;
import de.fraunhofer.iosb.maypadbackend.repositories.ProjectRepository;
import de.fraunhofer.iosb.maypadbackend.services.scheduler.SchedulerService;
import de.fraunhofer.iosb.maypadbackend.services.webhook.WebhookService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Service managing projects of a {@link Projectgroup}.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Service
@NoArgsConstructor
public class ProjectService {

    private WebhookService webhookService;
    private ProjectgroupService projectgroupService;
    private SchedulerService schedulerService;
    private ProjectRepository projectRepository;

    /**
     * Constructor for ProjectService.
     *
     * @param webhookService      Service for webhooks
     * @param projectgroupService Service for projectgroups
     * @param schedulerService    Service for scheduled tasks
     * @param projectRepository   Repository for database access
     */
    @Autowired
    public ProjectService(WebhookService webhookService, ProjectgroupService projectgroupService,
                          SchedulerService schedulerService, ProjectRepository projectRepository) {
        this.webhookService = webhookService;
        this.projectgroupService = projectgroupService;
        this.schedulerService = schedulerService;
        this.projectRepository = projectRepository;
    }

    /**
     * Create a project.
     *
     * @param projectgroupId Id of the project group to which the project belongs
     * @param repositoryUrl  Url to the repository
     * @return Created project
     */
    public Project create(int projectgroupId, String repositoryUrl) {
        //TODO: Webhook and Repo for model
        Project project = saveProject(new Project(repositoryUrl));
        addProjectToProjectgroup(projectgroupId, project);
        return project;
    }

    /**
     * Create a project.
     *
     * @param request Request with the needed data
     * @return Created project
     */
    public Project create(CreateProjectRequest request) {
        ServiceAccount serviceAccount = getServiceAccount(request.getServiceAccountRequest());
        Project project = saveProject(new Project(request.getRepositoryUrl(), serviceAccount));
        addProjectToProjectgroup(request.getGroupId(), project);
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
     * Change the data of a project.
     *
     * @param id      Id of the project
     * @param request Request with the data
     * @return Updated project
     */
    public Project changeProject(int id, ChangeProjectRequest request) {
        Project project = getProject(id);
        ServiceAccount serviceAccount = getServiceAccount(request.getServiceAccountRequest());
        project.setRepoUrl(request.getRepositoryUrl());
        project.setServiceAccount(serviceAccount);
        //TODO: Repo url ~> update Repo
        return saveProject(project);
    }

    /**
     * Delete a project.
     *
     * @param id Id of the project
     */
    public void deleteProject(int id) {
        getProject(id);
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
        if (request.getKey() != null
                && request.getKey().isPresent()) {
            serviceAccount = new KeyServiceAccount(request.getKey().get());
        } else if (request.getUserName() != null && request.getPassword() != null
                && request.getUserName().isPresent() && request.getPassword().isPresent()) {
            serviceAccount = new UserServiceAccount(request.getUserName().get(),
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
