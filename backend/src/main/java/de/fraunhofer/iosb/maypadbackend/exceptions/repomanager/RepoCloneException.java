package de.fraunhofer.iosb.maypadbackend.exceptions.repomanager;

public class RepoCloneException extends RepositoryException {

    public RepoCloneException(int projectid, String message) {
        super(projectid, message);
    }

}
