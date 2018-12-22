package de.fraunhofer.iosb.maypadbackend.model.person;

import lombok.Data;

import javax.persistence.Entity;

/**
 * Author for a commit
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Author {

    private Mail mail;

}
