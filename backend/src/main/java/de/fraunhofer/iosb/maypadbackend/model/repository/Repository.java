package de.fraunhofer.iosb.maypadbackend.model.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository of a version management system.
 *
 * @version 1.0
 */
@Data
@Entity
public class Repository {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Enumerated(EnumType.STRING)
    private RepositoryType repositoryType;

    @Column
    private String maypadConfigHash = null;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Tag> tags;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapKeyColumn(name = "name", length = 200)
    private Map<String, Branch> branches;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Commit lastCommit;

    /**
     * Constructor for Repository.
     */
    public Repository() {
        this(RepositoryType.NONE);
    }

    /**
     * Constructor for Repository.
     *
     * @param repositoryType Type of new repository
     */
    public Repository(RepositoryType repositoryType) {
        this.repositoryType = repositoryType;
        this.branches = new ConcurrentHashMap<>();
        this.tags = new ArrayList<>();
    }
}
