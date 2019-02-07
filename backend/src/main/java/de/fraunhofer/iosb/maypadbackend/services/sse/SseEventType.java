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
     * Projectconfig is invalid.
     */
    PROJECT_CONFIG_INVALID("project_config_invalid"),
    /**
     * The cap of maximum refresh-requests is reached.
     */
    PROJECT_CAP_REACHED("project_cap_reached"),
    /**
     * The repository url is invalid.
     */
    PROJECT_URL_INVALID("project_url_invalid"),
    /**
     * The refresh / init ended in an error.
     */
    PROJECT_REFRESH_FAILED("project_refresh_failed"),
    /**
     * The same project is currently updateing.
     */
    PROJECT_CURRENTLY_UPDATE("project_currently_update"),
    /**
     * IO error reading / writing ato the filesystem.
     */
    FILESYSTEM_ERROR("fileystem_error");


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
