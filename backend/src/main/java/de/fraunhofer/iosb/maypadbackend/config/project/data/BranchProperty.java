package de.fraunhofer.iosb.maypadbackend.config.project.data;

import lombok.Data;

import java.util.List;

/**
 * Class to store Branch properties as listed in Maypad YAML-File.
 */
@Data
public class BranchProperty {
    private String name;
    private String description;
    private List<String> members;
    private List<String> mails;
    private BuildProperty build;
    private DeploymentProperty deployment;
    private List<String> dependsOn;

}
