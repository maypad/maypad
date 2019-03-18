package de.fraunhofer.iosb.maypadbackend.exceptions.repomanager;

public class RepoManagerException extends Exception {

    private int projectId;
    private String event;

    /**
     * Generic constructor.
     *
     * @param projectId Reference to project where error occured.
     * @param event Event that should be sent as a SSE.
     */
    public RepoManagerException(int projectId, String event) {
        super("RepoManager threw an exception - see event-attribute for details.");
        this.projectId = projectId;
        this.event = event;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getEvent() {
        return event;
    }

}
