package de.fraunhofer.iosb.maypadbackend.config.server;

import java.util.List;

/**
 *  Interface for classes that implement the parsing of a Maypad configuration file.
 *
 * @author Max Willich
 */
public interface ServerConfig {

    String getProjectName();
    String getProjectDescription();
    boolean getAddAllBranches();
    List<BranchProperty> getBranchProperties();

}
