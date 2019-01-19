package de.fraunhofer.iosb.maypadbackend.controller.api;

import de.fraunhofer.iosb.maypadbackend.dtos.mapper.ProjectMapper;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectResponse;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ProjectController implements ProjectApi {

    private ProjectService projectService;
    private ProjectMapper projectMapper;

    /**
     * Constructor for ProjectController.
     * @param projectService the ProjectService used to access projects
     * @param projectMapper the ProjectMapper used to map projects to project-responses
     */
    @Autowired
    public ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    @Override
    public ProjectResponse createProject(@Valid CreateProjectRequest request) {
        return projectMapper.toResponse(projectService.create(request));
    }

    @Override
    public List<ProjectResponse> getProjects() {
        return projectMapper.toResponseList(projectService.getProjects());
    }

    @Override
    public ProjectResponse getProject(int id) {
        return projectMapper.toResponse(projectService.getProject(id));
    }

    @Override
    public ProjectResponse changeProject(int id, @Valid ChangeProjectRequest request) {
        return projectMapper.toResponse(projectService.changeProject(id, request));
    }

    @Override
    public void refreshProject(int id) {
        //TODO: Implement RepoService
    }

    @Override
    public void deleteProject(int id) {
        projectService.deleteProject(id);
    }
}
