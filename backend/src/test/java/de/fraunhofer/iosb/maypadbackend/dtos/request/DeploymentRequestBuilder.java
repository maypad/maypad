package de.fraunhofer.iosb.maypadbackend.dtos.request;

public final class DeploymentRequestBuilder {
    private boolean withDependencies;
    private boolean withBuild;

    private DeploymentRequestBuilder() {
    }

    public static DeploymentRequestBuilder create() {
        return new DeploymentRequestBuilder();
    }

    public DeploymentRequestBuilder withDependencies(boolean withDependencies) {
        this.withDependencies = withDependencies;
        return this;
    }

    public DeploymentRequestBuilder withBuild(boolean withBuild) {
        this.withBuild = withBuild;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public DeploymentRequest build() {
        DeploymentRequest deploymentRequest = new DeploymentRequest();
        deploymentRequest.setWithDependencies(withDependencies);
        deploymentRequest.setWithBuild(withBuild);
        return deploymentRequest;
    }
}
