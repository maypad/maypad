package de.fraunhofer.iosb.maypadbackend.model.serviceaccount;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Service account whose authentication is using an username and password.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserServiceAccount extends ServiceAccount {

    @Column(columnDefinition = "TEXT")
    private String password;
    @Column
    private String username;

    /**
     * Constructor for a Serviceaccount with username and password.
     *
     * @param username username of the serviceaccount
     * @param password Password of the serviceaccount
     */
    public UserServiceAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
