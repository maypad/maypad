package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

import de.fraunhofer.iosb.maypadbackend.services.reporefresh.BuildSystemManager;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.MavenManager;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Buildsystem Maven.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Maven extends BuildSystem {

    @Column
    private String groupId;
    @Column
    private String artifactId;
    @Column
    private String version;

    @Transient
    private MavenManager mavenManager;

    @Override
    public BuildSystemManager getManager() {
        if (mavenManager == null) {
            mavenManager = new MavenManager();
        }
        return mavenManager;
    }
}
