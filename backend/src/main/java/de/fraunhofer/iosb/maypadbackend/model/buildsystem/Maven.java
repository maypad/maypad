package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

import de.fraunhofer.iosb.maypadbackend.services.reporefresh.BuildSystemManager;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.MavenManager;
import lombok.Data;

import javax.persistence.Entity;

/**
 * Buildsystem Maven
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Maven extends BuildSystem {

    private String groupId;
    private String artifactId;
    private String version;
    private MavenManager mavenManager;

    @Override
    public BuildSystemManager getManager() {
        if (mavenManager == null) {
            mavenManager = new MavenManager();
        }
        return mavenManager;
    }
}
