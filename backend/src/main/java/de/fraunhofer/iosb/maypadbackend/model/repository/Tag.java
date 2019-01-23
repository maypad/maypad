package de.fraunhofer.iosb.maypadbackend.model.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Tag in a {@link Repository}. It can belong to a {@link Commit}.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
@NoArgsConstructor
public class Tag {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    private Commit commit;

    /**
     * Constructor for a Tag.
     *
     * @param name   Name of the tag
     * @param commit Commit for this tag
     */
    public Tag(String name, Commit commit) {
        this.name = name;
        this.commit = commit;
    }
}
