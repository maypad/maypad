package de.fraunhofer.iosb.maypadbackend.model.person;

import lombok.Data;

import javax.persistence.Entity;

/**
 * A general person
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class Person {

    private int id;
    private String name;

}
