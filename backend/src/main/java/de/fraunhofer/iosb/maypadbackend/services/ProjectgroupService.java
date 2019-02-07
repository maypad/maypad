package de.fraunhofer.iosb.maypadbackend.services;

import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.exceptions.httpexceptions.NotFoundException;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import de.fraunhofer.iosb.maypadbackend.repositories.ProjectgroupRepository;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;
import de.fraunhofer.iosb.maypadbackend.services.sse.EventData;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseEventType;
import de.fraunhofer.iosb.maypadbackend.services.sse.SseService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

/**
 * Service which manage the projectgroups.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Service
@NoArgsConstructor
public class ProjectgroupService {

    private ProjectgroupRepository projectgroupRepository;
    private RepoService repoService;
    private SseService sseService;

    /**
     * Constructor for ProjectgroupService.
     *
     * @param projectgroupRepository Repository for database access
     * @param sseService             Service for server-sent events
     */
    @Autowired
    public ProjectgroupService(ProjectgroupRepository projectgroupRepository, RepoService repoService, SseService sseService) {
        this.projectgroupRepository = projectgroupRepository;
        this.repoService = repoService;
    }

    /**
     * Get a list with all projectgroups.
     *
     * @return List with all projectgroups
     */
    public List<Projectgroup> getProjectgroups() {
        return projectgroupRepository.findAll();
    }

    /**
     * Create a projectgroup.
     *
     * @param name Name of the new Projectgroup
     * @return Created projectgroup
     */
    public Projectgroup create(String name) {
        return saveProjectgroup(new Projectgroup(name));
    }

    /**
     * Create a projectgroup.
     *
     * @param request Request with the data (e.g. projectgroupname)
     * @return Created projectgroup
     */
    public Projectgroup create(CreateProjectgroupRequest request) {
        return create(request.getName());
    }

    /**
     * Update a projectgroup.
     *
     * @param id      id of the projectgroup
     * @param request Request with the new data
     * @return Changed projectgroup
     */
    public Projectgroup changeProjectgroup(int id, ChangeProjectgroupRequest request) {
        Projectgroup projectgroup = getProjectgroup(id);
        projectgroup.setName(request.getName());
        return saveProjectgroup(projectgroup);
    }

    /**
     * Save a projectgroup.
     *
     * @param projectgroup Specific projectgroup
     * @return Saved projectgroup
     */
    public Projectgroup saveProjectgroup(Projectgroup projectgroup) {
        return projectgroupRepository.saveAndFlush(projectgroup);
    }

    /**
     * Delete a projectgroup.
     *
     * @param id Id of projectgroup
     */
    @Async
    public void deleteProjectgroup(int id) {
        Projectgroup group = getProjectgroup(id);
        for (Project project : group.getProjects()) {
            repoService.deleteProject(project.getId());
        }
        projectgroupRepository.deleteById(id);
    }

    /**
     * Get a projectgroup by id.
     *
     * @param id Id of the projectgrouop
     * @return Projectgroup with given id
     */
    public Projectgroup getProjectgroup(int id) {
        return (projectgroupRepository.findById(id).orElseThrow(projectGroupNotFoundException(id)));
    }

    /**
     * Get all projects of a projectgroup.
     *
     * @param id Id of projectgroup
     * @return List of all projects in a projectgroup
     */
    public List<Project> getProjects(int id) {
        Projectgroup projectgroup = getProjectgroup(id);
        return projectgroup.getProjects();
    }


    /**
     * Updates the status of the projectgroup with the given id.
     *
     * @param id the id of the projectgroup that should be updated
     */
    public void updateStatus(int id) {
        Projectgroup projectgroup = getProjectgroup(id);
        projectgroup.updateStatus();
        saveProjectgroup(projectgroup);
    }

    /**
     * Returns the projectgroup containing the given project.
     *
     * @param project the project that should be contained in the projectgroup
     * @return the projectgroup containing the given project
     */
    public Projectgroup getProjectrgroupByProject(Project project) {
        return projectgroupRepository.findProjectgroupByProjectsContaining(project)
                .orElseThrow(() -> new NotFoundException("PROJECTGROUP_NOT_FOUND",
                        String.format("Projectgroup containing Project with id %d not found", project.getId())));
    }

    /**
     * Throw an exception if the projectgroup was not found.
     *
     * @param id Id of the projectgroup
     * @return Represents a supplier for this exception
     */
    private Supplier<NotFoundException> projectGroupNotFoundException(int id) {
        return () -> new NotFoundException("PROJECT_NOT_FOUND",
                String.format("Projectgroup with id %d not found!", id));
    }


}