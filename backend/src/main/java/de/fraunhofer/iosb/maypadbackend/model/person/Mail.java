package de.fraunhofer.iosb.maypadbackend.model.person;

import lombok.Data;

import javax.persistence.Entity;

/**
 * An Email-Adress
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Mail {

    private int id;
    private String mailAddress;

}
