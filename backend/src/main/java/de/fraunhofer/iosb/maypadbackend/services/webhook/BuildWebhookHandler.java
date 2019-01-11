package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;

/**
 * WebhookHandler, that updates the build Status of a given branch.
 */
public class BuildWebhookHandler implements WebhookHandler {
    private Branch branch;
    private Status status;

    @Override
    public void handle() {
        //Implementation requires BuildService.
    }

    /**
     * Constructor for BuildWebhookHandler.
     * @param branch the branch that should be updated
     * @param status the new status of the latest build
     */
    BuildWebhookHandler(Branch branch, Status status) {
        this.branch = branch;
        this.status = status;
    }
}
