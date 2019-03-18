package de.fraunhofer.iosb.maypadbackend.exceptions.repomanager;

public class LastCommitException extends RepoManagerException {

    public LastCommitException(int projectId, String event) {
        super(projectId, event);
    }

}
