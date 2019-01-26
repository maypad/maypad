package de.fraunhofer.iosb.maypadbackend.services.build;

import de.fraunhofer.iosb.maypadbackend.model.build.BuildType;
import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;

public interface BuildTypeExecutor {
    /**
     * Builds a branch with the given buildType.
     * @param buildType the type that specifies how the branch should be build
     */
    public void build(BuildType buildType, Branch branch);
}
