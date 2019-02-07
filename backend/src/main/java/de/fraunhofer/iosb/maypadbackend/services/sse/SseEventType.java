package de.fraunhofer.iosb.maypadbackend.services.sse;

/**
 * All types for a server sent event.
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
    PROJECTGROUP_DELETED("projectgroup_deleted"),
    /**
     * Projectconfig is missing.
     */
    PROJECT_CONFIG_MISSING("project_config_missing"),
    /**
     * Projectconfig is invalid.
     */
    PROJECT_CONFIG_INVALID("project_config_invalid");


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
