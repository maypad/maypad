package de.fraunhofer.iosb.maypadbackend.model.deployment;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;

import java.util.Date;

public final class DeploymentBuilder {
    private int id;
    private Date timestamp;
    private Build build;
    private DeploymentType type;
    private Status status;

    private DeploymentBuilder() {
    }

    public static DeploymentBuilder create() {
        return new DeploymentBuilder();
    }

    public DeploymentBuilder id(int id) {
        this.id = id;
        return this;
    }

    public DeploymentBuilder timestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public DeploymentBuilder withBuild(Build build) {
        this.build = build;
        return this;
    }

    public DeploymentBuilder type(DeploymentType type) {
        this.type = type;
        return this;
    }

    public DeploymentBuilder status(Status status) {
        this.status = status;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public Deployment build() {
        Deployment deployment = new Deployment();
        deployment.setId(id);
        deployment.setTimestamp(timestamp);
        deployment.setBuild(build);
        deployment.setType(type);
        deployment.setStatus(status);
        return deployment;
    }
}
