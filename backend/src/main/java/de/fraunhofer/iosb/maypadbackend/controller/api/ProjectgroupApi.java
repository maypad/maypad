package de.fraunhofer.iosb.maypadbackend.controller.api;

import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectgroupRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectResponse;
import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectgroupResponse;

import java.util.List;

public interface ProjectgroupApi {
    /**
     * Creates a new projectgorup with the parameters set in the Request.
     * @param request the CreateRequest, that contains the required parameters
     * @return a ProjectgroupResponse of the newly created projectgroup
     */
    public ProjectgroupResponse createProjectgroup(CreateProjectgroupRequest request);

    /**
     * Returns a list of all projectgroups.
     * @return List of all projectgroups as {@link ProjectgroupResponse}.
     */
    public List<ProjectgroupResponse> getProjectgroups();

    /**
     * Returns the projectgroup with the given ID, if a projectgroup with the given ID exists.
     * @param id the id of the wanted projectgroup
     * @return ProjectgroupResponse of the wanted projectgroup
     */
    public ProjectgroupResponse getProjectgroup(int id);

    /**
     * Updates the projectgroup with the given ID with the values in the ChangeRequest.
     * @param id the ID of the projectgroup, that should be updated
     * @param request the request, that contains the new values.
     * @return the ProjectgroupResponse of the updated projectgroup.
     */
    public ProjectgroupResponse changeProjectgroup(int id, ChangeProjectgroupRequest request);

    /**
     * Deletes the projectgorup with the given ID, if a projectgroup with the given ID exists.
     * @param id the ID of the projectgorup, that should be deleted.
     */
    public void deleteProjectgroup(int id);

    /**
     * Returns a list of all projects contained in the projectgroup with the given ID,
     * if a projectgroup with the given ID exists.
     * @param id the id of the projectgroup, that contains the projects.
     * @return List of all projects that a part of the specified projectgroup.
     */
    public List<ProjectResponse> getProjects(int id);
}
