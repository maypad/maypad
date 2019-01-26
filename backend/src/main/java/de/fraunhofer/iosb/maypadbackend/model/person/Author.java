package de.fraunhofer.iosb.maypadbackend.model.person;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
@EqualsAndHashCode(callSuper = true)
public class Author extends Person {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
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
