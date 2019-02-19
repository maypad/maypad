package de.fraunhofer.iosb.maypadbackend.dtos.request;

public final class CreateProjectRequestBuilder {
    private int groupId;
    private String repositoryUrl;
    private ServiceAccountRequest serviceAccount;
    private String versionControlSystem;

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

    public CreateProjectRequestBuilder versionControlSystem(String versionControlSystem) {
        this.versionControlSystem = versionControlSystem;
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
        createProjectRequest.setVersionControlSystem(versionControlSystem);
        return createProjectRequest;
    }
}
