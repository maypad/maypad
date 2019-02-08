package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.services.reporefresh.RepoService;

/**
 * WebhookHandler, that refreshes a project.
 */
public class RefreshWebhookHandler implements WebhookHandler {
    private int projectId;
    private RepoService repoService;

    @Override
    public void handle() {
        repoService.refreshProject(projectId);
    }

    /**
     * Constructor for RefreshWebhookHandler.
     *
     * @param projectId   the project that should be refreshed
     * @param repoService the RepoService used to refresh a project
     */
    public RefreshWebhookHandler(int projectId, RepoService repoService) {
        this.projectId = projectId;
        this.repoService = repoService;
    }
}
