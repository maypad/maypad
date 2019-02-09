package de.fraunhofer.iosb.maypadbackend.model.serviceaccount;

import de.fraunhofer.iosb.maypadbackend.services.security.EncryptedText;
import de.fraunhofer.iosb.maypadbackend.services.security.EncryptionService;
import de.fraunhofer.iosb.maypadbackend.util.EncryptionServiceProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

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
    private String salt;
    @Column
    private String username;

    @Transient
    private EncryptionService encryptionService;

    /**
     * Constructor for a Serviceaccount with username and password.
     *
     * @param username username of the serviceaccount
     * @param password Password of the serviceaccount
     */
    public UserServiceAccount(String username, String password) {
        this.username = username;
        this.encryptionService = EncryptionServiceProvider.getEncryptionService();
        EncryptedText pass = encryptionService.encrypt(password);
        this.password = pass.getText();
        this.salt = pass.getSalt();
    }

    public String getPassword() {
        return encryptionService.decrypt(password, salt);
    }
}
