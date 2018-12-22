package de.fraunhofer.iosb.maypadbackend.model.repository;

import lombok.Data;

import javax.persistence.Entity;

/**
 * Tag in a {@link Repository}. It can belong to a {@link Commit}.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Tag {

    private int id;
    private String name;
    private Commit commit;

}
