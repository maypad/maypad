package de.fraunhofer.iosb.maypadbackend.exceptions.repomanager;

public class ConfigNotFoundException extends RepositoryException {

    public ConfigNotFoundException(int projectId, String message) {
        super(projectId, message);
    }

}
