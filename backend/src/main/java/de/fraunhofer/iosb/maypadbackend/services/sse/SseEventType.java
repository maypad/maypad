package de.fraunhofer.iosb.maypadbackend.services.sse;

/**
 * All types for a server-sent events.
 */
public enum SseEventType {

    /**
     * Thrown when a project was initialized.
     */
    PROJECT_INIT("project_init"),

    /**
     * Thrown when a project was refreshed.
     */
    PROJECT_REFRESHED("project_refreshed"),

    /**
     * Thrown when the authentication method was updated.
     */
    AUTHMETHOD_UPDATED("auth_updated"),

    /**
     * Thrown when a build status was updated.
     */
    BUILD_UPDATE("build_updated"),

    /**
     * Thrown when a deployment status was updated.
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
