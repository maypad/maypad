package de.fraunhofer.iosb.maypadbackend.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * A projectgroup in which can have {@link Project}s
 *
 * @author Lukas Brosch
 * @version 1.0
 */

@Data
@Entity
public class Projectgroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    @Basic
    private String name;
    @Enumerated(EnumType.STRING)
    private Status buildStatus;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Project> projects;

}
