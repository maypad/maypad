package de.fraunhofer.iosb.maypadbackend.controller.api;

import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectResponse;
import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectgroupResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * Api calls for projectgroups.
 */
@RequestMapping("/api/projectgroups")
public interface ProjectgroupApi {
    /**
     * Creates a new projectgorup with the parameters set in the Request.
     *
     * @param request the CreateRequest, that contains the required parameters
     * @return a ProjectgroupResponse of the newly created projectgroup
     */
    @PostMapping()
    ProjectgroupResponse createProjectgroup(@Valid @RequestBody CreateProjectgroupRequest request);

    /**
     * Returns a list of all projectgroups.
     *
     * @return List of all projectgroups as {@link ProjectgroupResponse}.
     */
    @GetMapping
    List<ProjectgroupResponse> getProjectgroups();

    /**
     * Returns the projectgroup with the given ID, if a projectgroup with the given ID exists.
     *
     * @param id the id of the wanted projectgroup
     * @return ProjectgroupResponse of the wanted projectgroup
     */
    @GetMapping("/{id}")
    ProjectgroupResponse getProjectgroup(@PathVariable int id);

    /**
     * Updates the projectgroup with the given ID with the values in the ChangeRequest.
     *
     * @param id      the ID of the projectgroup, that should be updated
     * @param request the request, that contains the new values.
     * @return the ProjectgroupResponse of the updated projectgroup.
     */
    @PutMapping("/{id}")
    ProjectgroupResponse changeProjectgroup(@PathVariable int id, @Valid @RequestBody ChangeProjectgroupRequest request);

    /**
     * Deletes the projectgorup with the given ID, if a projectgroup with the given ID exists.
     *
     * @param id the ID of the projectgorup, that should be deleted.
     */
    @DeleteMapping("/{id}")
    void deleteProjectgroup(@PathVariable int id);

    /**
     * Returns a list of all projects contained in the projectgroup with the given ID,
     * if a projectgroup with the given ID exists.
     *
     * @param id the id of the projectgroup, that contains the projects.
     * @return List of all projects that a part of the specified projectgroup.
     */
    @GetMapping("{id}/projects")
    List<ProjectResponse> getProjects(@PathVariable int id);
}
