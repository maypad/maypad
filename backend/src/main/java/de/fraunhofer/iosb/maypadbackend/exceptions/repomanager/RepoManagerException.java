package de.fraunhofer.iosb.maypadbackend.exceptions.repomanager;

/**
 * Exception that is thrown when an error occurs in a RepoManager.
 */
public class RepoManagerException extends Exception {

    private int projectId;
    private String eventMessage;

    /**
     * Generic constructor.
     *
     * @param projectId the id of the project where the error occurred
     * @param eventMessage the message of the event
     */
    public RepoManagerException(int projectId, String eventMessage) {
        super("RepoManager threw an exception - see event-attribute for details.");
        this.projectId = projectId;
        this.eventMessage = eventMessage;
    }

    /**
     * Return the id of the project that caused the exception.
     *
     * @return the id of the project
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Returns the event message.
     *
     * @return the event message
     */
    public String getEventMessage() {
        return eventMessage;
    }

}
