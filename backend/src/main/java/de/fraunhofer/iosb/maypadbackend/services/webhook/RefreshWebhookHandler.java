package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;

/**
 * WebhookHandler, that refreshes a project.
 */
public class RefreshWebhookHandler implements WebhookHandler {
    private Project project;
    private RepoService repoService;

    @Override
    public void handle() {
        repoService.refreshProject(project);
    }

    /**
     * Constructor for RefreshWebhookHandler.
     * @param project the project that should be refreshed
     * @param repoService the RepoService used to refresh a project
     */
    public RefreshWebhookHandler(Project project, RepoService repoService) {
        this.project = project;
        this.repoService = repoService;
    }
}
