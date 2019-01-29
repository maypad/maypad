package de.fraunhofer.iosb.maypadbackend.model.serviceaccount;

import de.fraunhofer.iosb.maypadbackend.services.security.EncryptedText;
import de.fraunhofer.iosb.maypadbackend.services.security.EncryptionService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

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
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class KeyServiceAccount extends ServiceAccount {

    @Column(name = "ssh_key", length = 1024)
    private String key;
    @Column
    String salt;

    @Autowired
    private EncryptionService encryptionService;

    /**
     * Constructor for a serviceaccount with a ssh key.
     *
     * @param sshKey ssh key
     */
    public KeyServiceAccount(String key) {
        EncryptedText text = encryptionService.encrypt(key);
        this.key = text.getText();
        this.salt = text.getSalt();
    }

    public String getKey() {
        return encryptionService.decrypt(key, salt);
    }
}
