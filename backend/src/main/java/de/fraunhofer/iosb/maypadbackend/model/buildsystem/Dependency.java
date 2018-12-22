package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

import lombok.Data;

import javax.persistence.Entity;

/**
 * Dependency that is stored in a buildsystem (no Maypad-dependency)
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public abstract class Dependency {

    private int id;
    private String version;

}
