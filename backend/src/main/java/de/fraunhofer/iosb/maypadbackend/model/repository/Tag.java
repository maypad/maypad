package de.fraunhofer.iosb.maypadbackend.model.repository;

import lombok.Data;

import javax.persistence.*;

/**
 * Tag in a {@link Repository}. It can belong to a {@link Commit}.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Basic
    private String name;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Commit commit;

}
