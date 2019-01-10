package de.fraunhofer.iosb.maypadbackend.config.server;

import lombok.Data;
import java.util.List;

/**
 * Class to store Branch properties as listed in Maypad YAML-File.
 *
 * @author: Max Willich
 */
@Data
public class BranchProperty {
    private String name;
    private String description;
    private List<String> members;
    private List<String> mails;
    private String build;
    private DeploymentProperty deployment;
    private List<String> dependsOn;

    @Data
    private class DeploymentProperty {
        private String deploymentName;
        private String url;
    }
}
