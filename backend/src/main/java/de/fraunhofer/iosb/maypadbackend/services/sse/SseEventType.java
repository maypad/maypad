package de.fraunhofer.iosb.maypadbackend.services.sse;

/**
 * All types for a serversentevents.
 */
public enum SseEventType {

    /**
     * A build is currently running.
     */
    BUILD_RUNNING("build_running"),
    /**
     * The build has finished.
     */
    BUILD_DONE("build_done"),
    /**
     * The project has refresh successfully.
     */
    PROJECT_REFRESHED("project_refreshed"),
    /**
     * The project was deleted.
     */
    PROJECT_DELETED("project_deleted"),
    /**
     * The projectgroup was deleted.
     */
    PROJECTGROUP_DELETED("projectgroup_deleted");

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
