package de.fraunhofer.iosb.maypadbackend.exceptions.repomanager;

public class LastCommitException extends RepositoryException {

    public LastCommitException(int projectId, String message) {
        super(projectId, message);
    }

}
