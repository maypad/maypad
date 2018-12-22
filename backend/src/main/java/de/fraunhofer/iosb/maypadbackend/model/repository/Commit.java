package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.person.Author;
import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;

/**
 * A commit in a repository
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Commit {

    private int id;
    private String commitMessage;
    private String commitIdentifier;
    private Date timestamp;
    private Author author;

}
