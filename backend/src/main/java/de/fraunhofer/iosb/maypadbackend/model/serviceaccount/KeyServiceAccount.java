package de.fraunhofer.iosb.maypadbackend.model.serviceaccount;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Service account whose authentication is using an SSH key.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@Entity
public class KeyServiceAccount extends ServiceAccount {

    @Column(name = "ssh_key")
    private String key;

}
