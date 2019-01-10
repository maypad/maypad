package de.fraunhofer.iosb.maypadbackend.model.person;

import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@Entity
public class Author extends Person {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Mail mail;

    /**
     * Constructor for Author.
     * @param name the authors name
     * @param mail the authors emailaddress
     */
    public Author(String name, Mail mail) {
        super(name);
        this.mail = mail;
    }
}
