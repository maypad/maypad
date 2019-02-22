package de.fraunhofer.iosb.maypadbackend.model.serviceaccount;

import de.fraunhofer.iosb.maypadbackend.services.security.EncryptedText;
import de.fraunhofer.iosb.maypadbackend.services.security.EncryptionService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Service account whose authentication is using an username and password.
 *
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
    private String salt;
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
        EncryptedText pass = EncryptionService.encryptText(password);
        this.password = pass.getText();
        this.salt = pass.getSalt();
    }

    public String getPassword() {
        return EncryptionService.decryptText(password, salt);
    }
}
