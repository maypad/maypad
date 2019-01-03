package de.fraunhofer.iosb.maypadbackend.model.build;

import de.fraunhofer.iosb.maypadbackend.model.repository.Branch;
import lombok.Data;

import javax.persistence.*;

/**
 * BuildTypes start / trigger a build
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public abstract class BuildType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    private String name;

    @OneToOne
    private Branch branch;

}
