package de.fraunhofer.iosb.maypadbackend.model.build;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * BuildTypes start / trigger a build.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public abstract class BuildType {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column
    private String name;

}
