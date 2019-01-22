package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildService;

/**
 * WebhookHandler, that updates the build Status of a given branch.
 */
public class BuildWebhookHandler implements WebhookHandler {
    private Branch branch;
    private Status status;
    private BuildService buildService;

    @Override
    public void handle() {
        buildService.signalStatus(branch, status);
    }

    /**
     * Constructor for BuildWebhookHandler.
     * @param branch the branch that should be updated
     * @param status the new status of the latest build
     * @param buildService the BuildService used to update the build status
     */
    BuildWebhookHandler(Branch branch, Status status, BuildService buildService) {
        this.branch = branch;
        this.status = status;
        this.buildService = buildService;
    }
}
