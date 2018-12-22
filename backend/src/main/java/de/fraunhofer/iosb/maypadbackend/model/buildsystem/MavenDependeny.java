package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

import lombok.Data;

import javax.persistence.Entity;

/**
 * Dependency for the {@link Maven} buildsystem
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class MavenDependeny extends Dependency {

    private String groupId;
    private String artifactId;
    private Scope scope;

}
