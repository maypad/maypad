package de.fraunhofer.iosb.maypadbackend.exceptions.repomanager;

/**
 * Exception that is thrown when an error occurs while cloning.
 */
public class RepoCloneException extends RepoManagerException {

    /**
     * Constructor for RepoCloneException.
     *
     * @param projectId the id of the project where the error occurred
     * @param eventMessage the message of the event
     */
    public RepoCloneException(int projectId, String eventMessage) {
        super(projectId, eventMessage);
    }

}
