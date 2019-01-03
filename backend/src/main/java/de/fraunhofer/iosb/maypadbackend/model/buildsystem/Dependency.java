package de.fraunhofer.iosb.maypadbackend.model.buildsystem;

import lombok.Data;

import javax.persistence.*;

/**
 * Dependency that is stored in a buildsystem (no Maypad-dependency)
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Dependency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column
    private String version;

    @ManyToOne
    BuildSystem buildSystem;

}
