package de.fraunhofer.iosb.maypadbackend.model.repository;

import lombok.Data;

import javax.persistence.*;
import java.io.File;
import java.util.List;

/**
 * Repository of a version management system
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Basic
    private File rootFolder; //TODO: File <-> Path convert

    @Enumerated(EnumType.STRING)
    private RepositoryType repositoryType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Tag> tags;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Branch> branches;

}
