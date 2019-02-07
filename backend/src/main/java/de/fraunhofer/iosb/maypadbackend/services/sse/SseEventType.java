package de.fraunhofer.iosb.maypadbackend.services.sse;

/**
 * All types for a server-sent events.
 */
public enum SseEventType {

    PROJECT_INIT("project_init"),

    PROJECT_REFRESHED("project_refreshed"),

    AUTHMETHOD_UPDATED("auth_updated"),

    BUILD_UPDATE("build_updated"),

    DEPLOYMENT_UPDATE("deployment_updated");

    private String eventId;

    SseEventType(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    @Override
    public String toString() {
        return eventId;
    }
}
