package de.fraunhofer.iosb.maypadbackend.model.person;

import lombok.Data;

import javax.persistence.*;

/**
 * A general person
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Basic
    private String name;

}
