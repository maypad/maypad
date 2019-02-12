package de.fraunhofer.iosb.maypadbackend.dtos.mapper.request;

import de.fraunhofer.iosb.maypadbackend.dtos.request.BuildRequest;

public final class BuildRequestBuilder {
    private boolean withDependencies;

    private BuildRequestBuilder() {
    }

    public static BuildRequestBuilder create() {
        return new BuildRequestBuilder();
    }

    public BuildRequestBuilder withDependencies(boolean withDependencies) {
        this.withDependencies = withDependencies;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public BuildRequest build() {
        BuildRequest buildRequest = new BuildRequest();
        buildRequest.setWithDependencies(withDependencies);
        return buildRequest;
    }
}
