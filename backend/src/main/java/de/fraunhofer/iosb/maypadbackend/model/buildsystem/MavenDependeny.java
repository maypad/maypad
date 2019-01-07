package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Dependency for the {@link Maven} buildsystem.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class MavenDependeny extends Dependency {

    @Column
    private String groupId;
    @Column
    private String artifactId;
    @Enumerated(EnumType.STRING)
    private Scope scope;

}
