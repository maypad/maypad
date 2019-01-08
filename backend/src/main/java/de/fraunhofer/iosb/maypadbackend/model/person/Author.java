package de.fraunhofer.iosb.maypadbackend.model.person;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

/**
 * Author for a commit.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Author extends Person {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Mail mail;

}
