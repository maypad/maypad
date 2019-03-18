package de.fraunhofer.iosb.maypadbackend.exceptions.repomanager;

public class RepoCloneException extends RepoManagerException {

    public RepoCloneException(int projectid, String event) {
        super(projectid, event);
    }

}
