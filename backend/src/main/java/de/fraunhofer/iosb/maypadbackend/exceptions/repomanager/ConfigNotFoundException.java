package de.fraunhofer.iosb.maypadbackend.exceptions.repomanager;

/**
 * Exception that is thrown when there is no config file present.
 */
public class ConfigNotFoundException extends RepoManagerException {

    /**
     * Constructor for RepoCloneException.
     *
     * @param projectId the id of the project where the error occurred
     * @param eventMessage the message of the event
     */
    public ConfigNotFoundException(int projectId, String eventMessage) {
        super(projectId, eventMessage);
    }

}
