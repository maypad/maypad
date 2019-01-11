package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.model.Project;

/**
 * WebhookHandler, that refreshes a project.
 */
public class RefreshWebhookHandler implements WebhookHandler {
    private Project project;

    @Override
    public void handle() {
        //Implementation requires RepoService
    }

    /**
     * Constructor for RefreshWebhookHandler.
     * @param project the project that should be refreshed.
     */
    public RefreshWebhookHandler(Project project) {
        this.project = project;
    }
}
