package de.fraunhofer.iosb.maypadbackend.exceptions.repomanager;

public class RepositoryException extends Exception {

    private int projectId;

    public RepositoryException(int projectId, String message) {
        super(message);
        this.projectId = projectId;
    }

    public int getProjectId() {
        return projectId;
    }

}
