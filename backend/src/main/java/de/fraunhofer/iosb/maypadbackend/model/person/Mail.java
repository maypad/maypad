package de.fraunhofer.iosb.maypadbackend.model.person;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * An Email-address.
 *
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
public class Mail {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    @Column
    private String mailAddress;

    /**
     * Constructor for Mail.
     *
     * @param mailAddress the Email-address.
     */
    public Mail(String mailAddress) {
        this.mailAddress = mailAddress;
    }
}
