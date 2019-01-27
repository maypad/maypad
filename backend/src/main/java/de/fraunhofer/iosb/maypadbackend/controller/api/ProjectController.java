package de.fraunhofer.iosb.maypadbackend.controller.api;

import de.fraunhofer.iosb.maypadbackend.dtos.mapper.ProjectMapper;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectResponse;
import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.services.ProjectService;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ProjectController implements ProjectApi {

    private ProjectService projectService;
    private ProjectMapper projectMapper;
    private RepoService repoService;

    /**
     * Constructor for ProjectController.
     *
     * @param projectService the ProjectService used to access projects
     * @param projectMapper  the ProjectMapper used to map projects to project-responses
     * @param repoService    Service for repository-operations
     */
    @Autowired
    public ProjectController(ProjectService projectService, ProjectMapper projectMapper, RepoService repoService) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.repoService = repoService;
    }

    @Override
    public ProjectResponse createProject(@Valid CreateProjectRequest request) {
        Project project = projectService.create(request);
        repoService.initProject(project);
        return projectMapper.toResponse(project);
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

        repoService.refreshProject(projectService.getProject(id));
    }

    @Override
    public void deleteProject(int id) {
        repoService.deleteProject(id);
    }
}
