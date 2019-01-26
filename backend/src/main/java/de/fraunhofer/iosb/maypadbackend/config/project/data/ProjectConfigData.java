package de.fraunhofer.iosb.maypadbackend.config.project.data;

import lombok.Data;

import java.util.List;

/**
 * Class to store project properties as listed in Maypad YAML-File.
 */
@Data
public class ProjectConfigData {
    private String projectName;
    private String projectDescription;
    private boolean allBranches;
    private List<BranchProperty> branches;
    private String repoWebsiteUrl;
    private String svnTrunkDirectory;
    private String svnBranchDirectory;
    private String svnTagsDirectory;
}
