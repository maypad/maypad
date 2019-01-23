package de.fraunhofer.iosb.maypadbackend.config.project.data;

import lombok.Data;

/**
 * Property for a deployment.
 */
@Data
public class DeploymentProperty {
    private String type;
    private String name;
    private String arguments;
}
