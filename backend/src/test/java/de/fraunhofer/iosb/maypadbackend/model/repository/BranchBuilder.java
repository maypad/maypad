package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.build.Build;
import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.deployment.Deployment;
import de.fraunhofer.iosb.maypadbackend.model.deployment.DeploymentType;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;

import java.util.List;

public final class BranchBuilder {
    private int id;
    //repository
    private String name;
    private String description;
    private String readme;
    private Commit lastCommit;
    //maypad-data
    private List<Person> members;
    private List<Mail> mails;
    private List<DependencyDescriptor> dependencies;
    //build
    private BuildType buildType;
    private List<Build> builds;
    private Status buildStatus;
    //deployment
    private DeploymentType deploymentType;
    private List<Deployment> deployments;
    //webhooks
    private InternalWebhook buildSuccessWebhook;
    private InternalWebhook buildFailureWebhook;

    private BranchBuilder() {
    }

    public static BranchBuilder create() {
        return new BranchBuilder();
    }

    public BranchBuilder id(int id) {
        this.id = id;
        return this;
    }

    public BranchBuilder name(String name) {
        this.name = name;
        return this;
    }

    public BranchBuilder description(String description) {
        this.description = description;
        return this;
    }

    public BranchBuilder readme(String readme) {
        this.readme = readme;
        return this;
    }

    public BranchBuilder lastCommit(Commit lastCommit) {
        this.lastCommit = lastCommit;
        return this;
    }

    public BranchBuilder members(List<Person> members) {
        this.members = members;
        return this;
    }

    public BranchBuilder mails(List<Mail> mails) {
        this.mails = mails;
        return this;
    }

    public BranchBuilder dependencies(List<DependencyDescriptor> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public BranchBuilder buildType(BuildType buildType) {
        this.buildType = buildType;
        return this;
    }

    public BranchBuilder builds(List<Build> builds) {
        this.builds = builds;
        return this;
    }

    public BranchBuilder buildStatus(Status buildStatus) {
        this.buildStatus = buildStatus;
        return this;
    }

    public BranchBuilder deploymentType(DeploymentType deploymentType) {
        this.deploymentType = deploymentType;
        return this;
    }

    public BranchBuilder deployments(List<Deployment> deployments) {
        this.deployments = deployments;
        return this;
    }

    public BranchBuilder buildSuccessWebhook(InternalWebhook buildSuccessWebhook) {
        this.buildSuccessWebhook = buildSuccessWebhook;
        return this;
    }

    public BranchBuilder buildFailureWebhook(InternalWebhook buildFailureWebhook) {
        this.buildFailureWebhook = buildFailureWebhook;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public Branch build() {
        Branch branch = new Branch();
        branch.setId(id);
        branch.setName(name);
        branch.setDescription(description);
        branch.setReadme(readme);
        branch.setLastCommit(lastCommit);
        branch.setMembers(members);
        branch.setMails(mails);
        branch.setDependencies(dependencies);
        branch.setBuildType(buildType);
        branch.setBuilds(builds);
        branch.setBuildStatus(buildStatus);
        branch.setDeploymentType(deploymentType);
        branch.setDeployments(deployments);
        branch.setBuildSuccessWebhook(buildSuccessWebhook);
        branch.setBuildFailureWebhook(buildFailureWebhook);
        return branch;
    }
}
