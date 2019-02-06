package de.fraunhofer.iosb.maypadbackend.services.sse;


public enum SseEventType {

    BUILD_RUNNING("build_running"),

    BUILD_DONE("build_done"),

    PROJECT_REFRESHED("project_refreshed"),

    PROJECT_DELETED("project_deleted"),

    PROJECTGROUP_DELETED("projectgroup_deleted");

    private String eventId;

    private SseEventType(String eventId) {
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
