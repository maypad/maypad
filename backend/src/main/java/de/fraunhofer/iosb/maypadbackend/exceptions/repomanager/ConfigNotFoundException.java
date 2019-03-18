package de.fraunhofer.iosb.maypadbackend.exceptions.repomanager;

public class ConfigNotFoundException extends RepoManagerException {

    public ConfigNotFoundException(int projectId, String event) {
        super(projectId, event);
    }

}
