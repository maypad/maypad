package de.fraunhofer.iosb.maypadbackend.controller.api;

import de.fraunhofer.iosb.maypadbackend.dtos.request.ChangeProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.response.ProjectResponse;

import java.util.List;


public interface ProjectApi {
    /**
     * Creates a new project with the parameters set in the CreateRequest.
     * @param request the CreateRequest, that contains the required values
     * @return the newly created Project as {@link ProjectResponse}
     */
    public ProjectResponse createProject(CreateProjectRequest request);

    /**
     * Returns a list of all projects.
     * @return List of all projects as {@link ProjectResponse}
     */
    public List<ProjectResponse> getProjects();

    /**
     * Returns the project with the given ID, if a project with the given ID exists.
     * @param id the ID of the wanted project
     * @return the project with the given ID as {@link ProjectResponse}
     */
    public ProjectResponse getProject(int id);

    /**
     * Updates the project with the given ID with the values specified in the ChangeRequest.
     * @param id the ID of the project, that should be updated
     * @param request the ChangeRequest, that contains the updated values
     * @return the project, that was updated
     */
    public ProjectResponse changeProject(int id, ChangeProjectRequest request);

    /**
     * Requests an update of the repository associated with project specified by the given ID,
     * if such a project exists.
     * @param id the id of the project, that should be updated
     */
    public void refreshProject(int id);

    /**
     * Deletes the project with the given ID, if such a project exists.
     * @param id the ID of the project, that should be deleted
     */
    public void deleteProject(int id);

}
