package de.fraunhofer.iosb.maypadbackend.model.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
import java.util.List;
import java.util.Map;

/**
 * Repository of a version management system.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
@NoArgsConstructor
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

    //@ElementCollection(fetch = FetchType.EAGER)
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapKeyColumn(name = "name", length = 200)
    private Map<String, Branch> branches;


}
