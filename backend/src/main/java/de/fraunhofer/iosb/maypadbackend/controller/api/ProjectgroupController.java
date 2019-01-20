package de.fraunhofer.iosb.maypadbackend.controller.api;

import de.fraunhofer.iosb.maypadbackend.dtos.mapper.ProjectMapper;
import de.fraunhofer.iosb.maypadbackend.dtos.mapper.ProjectgroupMapper;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectResponse;
import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectgroupResponse;
import de.fraunhofer.iosb.maypadbackend.services.ProjectgroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ProjectgroupController implements ProjectgroupApi {

    private ProjectgroupService projectgroupService;
    private ProjectgroupMapper projectgroupMapper;
    private ProjectMapper projectMapper;

    /**
     * Constructor for ProjectgroupController.
     * @param projectgroupService the ProjectgroupService used to access projectgroups
     * @param projectgroupMapper the ProjectgroupMapper used to map projectgroups to projectgroup-responses
     * @param projectMapper the ProjectMapper used to map projects to project-responses
     */
    @Autowired
    public ProjectgroupController(ProjectgroupService projectgroupService, ProjectgroupMapper projectgroupMapper,
                                  ProjectMapper projectMapper) {
        this.projectgroupService = projectgroupService;
        this.projectgroupMapper = projectgroupMapper;
        this.projectMapper = projectMapper;
    }

    @Override
    public ProjectgroupResponse createProjectgroup(@Valid @RequestBody CreateProjectgroupRequest request) {
        return projectgroupMapper.toResponse(projectgroupService.create(request));
    }

    @Override
    public List<ProjectgroupResponse> getProjectgroups() {
        return projectgroupMapper.toResponseList(projectgroupService.getProjectgroups());
    }

    @Override
    public ProjectgroupResponse getProjectgroup(@PathVariable int id) {
        return projectgroupMapper.toResponse(projectgroupService.getProjectgroup(id));
    }

    @Override
    public ProjectgroupResponse changeProjectgroup(@PathVariable int id,
                                                   @Valid @RequestBody ChangeProjectgroupRequest request) {
        return projectgroupMapper.toResponse(projectgroupService.changeProjectgroup(id, request));
    }

    @Override
    public void deleteProjectgroup(@PathVariable  int id) {
        projectgroupService.deleteProjectgroup(id);
    }

    @Override
    public List<ProjectResponse> getProjects(@PathVariable int id) {
        return projectMapper.toResponseList(projectgroupService.getProjects(id));
    }
}
