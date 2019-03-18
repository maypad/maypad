package de.fraunhofer.iosb.maypadbackend.services.sse;

/**
 * All types for a server-sent events.
 */
public enum SseEventType {

    /**
     * Thrown when a project was initialized.
     * Possible events:
     * - "init_successful": When a project was initialized successfully.
     */
    PROJECT_INIT("project_init"),

    /**
     * Thrown when a project was refreshed.
     * Possible events:
     * - "refresh_successful": When the refresh was successful.
     * - "bad_url": When the URL is not a valid url (bogus).
     * - "connection_refused": When no connection to the server could be established.
     * - "404_not_found": When server returns a 404 (possibly authentication problem)
     * - "auth_failed": When authentication failed.
     * - "clone_failed_unknown_reason": When cloning the repository failed by some other cause.
     * - "config_missing": When no maypad.yaml was found in root folder.
     */
    PROJECT_REFRESHED("project_refreshed"),

    /**
     * Thrown when the authentication method was updated.
     * Possible events:
     * - "serviceaccount_changed": When the Service Account was changed.
     */
    PROJECT_CHANGED("project_changed"),

    /**
     * Thrown when a build status was updated.
     * Possible events:
     * - "build_failed": When the build failed.
     * - "build_successful": When the build was successful.
     * - "build_timeout": When the build timed out.
     * - "build_running": When the build is still running.
     * - "build_unknown": When none of the above apply.
     */
    BUILD_UPDATE("build_updated"),

    /**
     * Thrown when a deployment status was updated.
     * Possible events:
     * - "deployment_failed": When the deployment failed.
     * - "deployment_successful": When the deployment was successful.
     * - "deployment_timeout": When the deployment timed out.
     * - "deployment_running": When the deployment is still running.
     * - "deployment_unknown": When none of the above apply.
     */
    DEPLOYMENT_UPDATE("deployment_updated");


    private String eventId;

    SseEventType(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Return the event id.
     *
     * @return the event id
     */
    public String getEventId() {
        return eventId;
    }

    @Override
    public String toString() {
        return eventId;
    }
}
