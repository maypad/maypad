package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import java.io.File;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column
    private File rootFolder; //TODO: need to save in DB?

    @Enumerated(EnumType.STRING)
    private Status repositoryStatus;

    @Enumerated(EnumType.STRING)
    private RepositoryType repositoryType;

    @Column
    private String maypadConfigHash = null;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Tag> tags;

    @ElementCollection()
    @MapKeyColumn(name = "name", length = 200)
    private Map<String, Branch> branches;


}
