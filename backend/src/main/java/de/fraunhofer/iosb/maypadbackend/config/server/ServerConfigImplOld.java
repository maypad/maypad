package de.fraunhofer.iosb.maypadbackend.config.server;

import de.fraunhofer.iosb.maypadbackend.config.server.data.BranchProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@PropertySource("classpath:maypad.yml")
@ConfigurationProperties(prefix = "maypad")
@Data
public class ServerConfigImplOld implements ServerConfig {

    private String projectName;
    private String projectDescription;
    private boolean allBranches;
    private List<BranchProperty> branches;

    /**
     * Returns project name configured in Maypad
     * Property: maypad.projectName
     *
     * @return The project name
     */
    @Override
    public String getProjectName() {
        return projectName;
    }

    /**
     * Returns project description configured in Maypad
     * Property: maypad.projectDescription
     *
     * @return The project description
     */
    @Override
    public String getProjectDescription() {
        return projectDescription;
    }

    /**
     * Returns property, whether all branches should be added to project or not
     * Property: maypad.allBranches
     *
     * @return true if all branches of repository should be added to project, false otherwise
     */
    @Override
    public boolean getAddAllBranches() {
        return allBranches;
    }

    /**
     * List of all Branches specified in property file.
     * Conversion to Branch-Entity happens in service-level.
     * Property:
     *
     * <p>maypad:
     *   branches:
     *     - master:
     *         name: master
     *         description: ...
     *         members:
     *           - ...
     *           - ...
     *         mails:
     *           - ...
     *           - ...
     *         ...
     *
     * @return List of BranchProperty-Objects describing branches.
     */
    @Override
    public List<BranchProperty> getBranchProperties() {
        return branches;
    }
}
