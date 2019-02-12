package de.fraunhofer.iosb.maypadbackend.dtos.mapper.request;

import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectRequest;
import de.fraunhofer.iosb.maypadbackend.dtos.request.ServiceAccountRequest;

public final class CreateProjectRequestBuilder {
    private int groupId;
    private String repositoryUrl;
    private ServiceAccountRequest serviceAccount;

    private CreateProjectRequestBuilder() {
    }

    public static CreateProjectRequestBuilder create() {
        return new CreateProjectRequestBuilder();
    }

    public CreateProjectRequestBuilder groupId(int groupId) {
        this.groupId = groupId;
        return this;
    }

    public CreateProjectRequestBuilder repositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
        return this;
    }

    public CreateProjectRequestBuilder serviceAccount(ServiceAccountRequest serviceAccount) {
        this.serviceAccount = serviceAccount;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public CreateProjectRequest build() {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setGroupId(groupId);
        createProjectRequest.setRepositoryUrl(repositoryUrl);
        createProjectRequest.setServiceAccount(serviceAccount);
        return createProjectRequest;
    }
}
