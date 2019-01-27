package de.fraunhofer.iosb.maypadbackend.config.project.data;

import lombok.Data;

@Data
public class DeploymentProperty {
    private String type;
    private String name;
    private String arguments;
}
