package de.fraunhofer.iosb.maypadbackend.model;

import de.fraunhofer.iosb.maypadbackend.model.repository.Repository;
import de.fraunhofer.iosb.maypadbackend.model.serviceaccount.ServiceAccount;
import de.fraunhofer.iosb.maypadbackend.model.webhook.InternalWebhook;

import java.util.Date;

public final class ProjectBuilder {
    private int id;
    private String name;
    private String description;
    private Date lastUpdate;
    private Status buildStatus;
    //repository
    private Repository repository;
    private String repositoryUrl;
    private String repoWebsiteUrl;
    private ServiceAccount serviceAccount;
    private Status repositoryStatus;
    //webhooks
    private InternalWebhook refreshWebhook;

    private ProjectBuilder() {
    }

    public static ProjectBuilder create() {
        return new ProjectBuilder();
    }

    public ProjectBuilder id(int id) {
        this.id = id;
        return this;
    }

    public ProjectBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProjectBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProjectBuilder lastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public ProjectBuilder buildStatus(Status buildStatus) {
        this.buildStatus = buildStatus;
        return this;
    }

    public ProjectBuilder repository(Repository repository) {
        this.repository = repository;
        return this;
    }

    public ProjectBuilder repositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
        return this;
    }

    public ProjectBuilder repoWebsiteUrl(String repoWebsiteUrl) {
        this.repoWebsiteUrl = repoWebsiteUrl;
        return this;
    }

    public ProjectBuilder serviceAccount(ServiceAccount serviceAccount) {
        this.serviceAccount = serviceAccount;
        return this;
    }

    public ProjectBuilder repositoryStatus(Status repositoryStatus) {
        this.repositoryStatus = repositoryStatus;
        return this;
    }

    public ProjectBuilder refreshWebhook(InternalWebhook refreshWebhook) {
        this.refreshWebhook = refreshWebhook;
        return this;
    }

    /**
     * Build the object.
     * @return the built object.
     */
    public Project build() {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setDescription(description);
        project.setLastUpdate(lastUpdate);
        project.setBuildStatus(buildStatus);
        project.setRepository(repository);
        project.setRepositoryUrl(repositoryUrl);
        project.setRepoWebsiteUrl(repoWebsiteUrl);
        project.setServiceAccount(serviceAccount);
        project.setRepositoryStatus(repositoryStatus);
        project.setRefreshWebhook(refreshWebhook);
        return project;
    }
}
