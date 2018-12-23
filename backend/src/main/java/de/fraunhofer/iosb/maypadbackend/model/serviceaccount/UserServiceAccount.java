package de.fraunhofer.iosb.maypadbackend.model.serviceaccount;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Service account whose authentication is using an username and password
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class UserServiceAccount extends ServiceAccount {

    @Column
    private String password;
    @Column
    private String username;

}
