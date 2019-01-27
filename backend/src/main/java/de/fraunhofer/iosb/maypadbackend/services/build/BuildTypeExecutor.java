package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;

public interface BuildTypeExecutor {
    /**
     * Builds a branch with the given buildType.
     * @param buildType the type that specifies how the branch should be build
     * @param id the id of the project
     * @param ref the name of the Branch
     */
    public void build(BuildType buildType, int id, String ref);
}
