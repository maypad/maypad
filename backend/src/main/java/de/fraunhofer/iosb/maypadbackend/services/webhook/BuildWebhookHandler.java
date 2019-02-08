package de.fraunhofer.iosb.maypadbackend.services.webhook;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import de.fraunhofer.iosb.maypadbackend.services.build.BuildService;
import de.fraunhofer.iosb.maypadbackend.util.Tuple;

/**
 * WebhookHandler, that updates the build Status of a given branch.
 */
public class BuildWebhookHandler implements WebhookHandler {
    private Tuple<Integer, String> branch;
    private Status status;
    private BuildService buildService;

    @Override
    public void handle() {
        buildService.signalStatus(branch.getKey(), branch.getValue(), status);
    }

    /**
     * Constructor for BuildWebhookHandler.
     *
     * @param branch       the branch that should be updated as a pair of project id and branch name
     * @param status       the new status of the latest build
     * @param buildService the BuildService used to update the build status
     */
    BuildWebhookHandler(Tuple<Integer, String> branch, Status status, BuildService buildService) {
        this.branch = branch;
        this.status = status;
        this.buildService = buildService;
    }
}
