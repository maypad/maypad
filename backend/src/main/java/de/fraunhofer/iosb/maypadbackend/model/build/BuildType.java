package de.fraunhofer.iosb.maypadbackend.model.build;

import lombok.Data;

import javax.persistence.Entity;

/**
 * BuildTypes start / trigger a build
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public abstract class BuildType {

    private int id;
    private String name;

}
