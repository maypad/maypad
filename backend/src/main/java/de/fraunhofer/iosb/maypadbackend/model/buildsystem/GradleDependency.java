package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Dependency for the {@link Gradle} buildsystem.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class GradleDependency extends Dependency {

    @Column(name = "dependeny_name")
    private String name;
    @Column(name = "dependeny_group")
    private String group;

}
