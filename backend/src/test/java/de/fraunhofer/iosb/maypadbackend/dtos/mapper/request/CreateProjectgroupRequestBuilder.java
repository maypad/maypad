package de.fraunhofer.iosb.maypadbackend.dtos.mapper.request;

import de.fraunhofer.iosb.maypadbackend.dtos.request.CreateProjectgroupRequest;

public final class CreateProjectgroupRequestBuilder {
    private String name;

    private CreateProjectgroupRequestBuilder() {
    }

    public static CreateProjectgroupRequestBuilder create() {
        return new CreateProjectgroupRequestBuilder();
    }

    public CreateProjectgroupRequestBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public CreateProjectgroupRequest build() {
        CreateProjectgroupRequest createProjectgroupRequest = new CreateProjectgroupRequest();
        createProjectgroupRequest.setName(name);
        return createProjectgroupRequest;
    }
}
