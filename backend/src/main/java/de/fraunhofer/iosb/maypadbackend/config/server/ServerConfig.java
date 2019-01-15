package de.fraunhofer.iosb.maypadbackend.config.server;

import de.fraunhofer.iosb.maypadbackend.config.server.data.BranchProperty;

import java.util.List;

/**
 *  Interface for classes that implement the parsing of a Maypad configuration file.
 *
 * @author Max Willich
 */
public interface ServerConfig {

    /**
     * Returns project name configured in Maypad.
     * @return The project name.
     */
    String getProjectName();

    /**
     * Returns project description configured in Maypad.
     * @return The project description.
     */
    String getProjectDescription();

    /**
     * Returns property, whether all branches should be added to project or not.
     *
     * @return true if all branches of repository should be added to project, false otherwise.
     */
    boolean getAddAllBranches();

    /**
     * List of all Branches specified in property file.
     * Conversion to Branch-Entity happens in service-level.
     *
     * @return List of BranchProperty-Objects describing branches.
     */
    List<BranchProperty> getBranchProperties();

}
