package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

import de.fraunhofer.iosb.maypadbackend.services.reporefresh.BuildSystemManager;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.GradleManager;
import lombok.Data;

import javax.persistence.Entity;

/**
 * Buildsystem Gradle
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Gradle extends BuildSystem {

    private String version;
    private GradleManager gradleManager;

    @Override
    public BuildSystemManager getManager() {
        if (gradleManager == null) {
            gradleManager = new GradleManager();
        }
        return gradleManager;
    }
}
