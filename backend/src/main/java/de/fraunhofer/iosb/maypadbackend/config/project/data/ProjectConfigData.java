package de.fraunhofer.iosb.maypadbackend.config.project.data;

import lombok.Data;

import java.util.List;

@Data
public class ProjectConfigData {
    private String projectName;
    private String projectDescription;
    private boolean allBranches;
    private List<BranchProperty> branches;
    private String svnTrunkDirectory;
    private String svnBranchDirectory;
    private String svnTagsDirectory;

    public boolean getAllBranches() {
        return allBranches;
    }
}
