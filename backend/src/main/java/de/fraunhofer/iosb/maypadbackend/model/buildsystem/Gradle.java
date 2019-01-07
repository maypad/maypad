package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

import de.fraunhofer.iosb.maypadbackend.services.reporefresh.BuildSystemManager;
import de.fraunhofer.iosb.maypadbackend.services.reporefresh.GradleManager;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Buildsystem Gradle.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Gradle extends BuildSystem {

    @Column
    private String version;

    @Transient
    private GradleManager gradleManager;

    @Override
    public BuildSystemManager getManager() {
        if (gradleManager == null) {
            gradleManager = new GradleManager();
        }
        return gradleManager;
    }
}
