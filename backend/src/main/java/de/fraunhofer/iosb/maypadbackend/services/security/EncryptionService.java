package de.fraunhofer.iosb.maypadbackend.services.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class EncryptionService {

    @Value("${MAYPAD_HOME:/usr/share/maypad/}")
    private String maypadHomePath;

    private String key;

    private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);

    /**
     * Returns a encrypted text containing the used salt.
     * @param text the string that should be encrypted
     * @return encryptedtext that contains the used salt
     */
    public EncryptedText encrypt(String text) {
        StringKeyGenerator generator = KeyGenerators.string();
        String salt = generator.generateKey();
        TextEncryptor textEncryptor = Encryptors.text(key, salt);
        return new EncryptedText(textEncryptor.encrypt(text), salt);
    }

    /**
     * Decrypts the given text with the salt.
     * @param encryptedText the encrypted string
     * @param salt the salt used for encrypting the text
     * @return
     */
    public String decrypt(String encryptedText, String salt) {
        TextEncryptor textEncryptor = Encryptors.text(key, salt);
        return textEncryptor.decrypt(encryptedText);
    }


    @PostConstruct
    private void init() {
        File maypadHome = new File(maypadHomePath);
        if (maypadHome.isDirectory()) {
            File security = new File(maypadHome.getAbsolutePath() + "/security/");
            security.mkdir();
            File encryptionKey = new File(security.getAbsolutePath() + "/key.dat");
            if (!encryptionKey.isFile()) {
                try {
                    StringKeyGenerator generator = KeyGenerators.string();
                    key = generator.generateKey();
                    Files.write(Paths.get(encryptionKey.getAbsolutePath()), key.getBytes(StandardCharsets.UTF_8),
                            StandardOpenOption.CREATE);
                } catch (IOException e) {
                    logger.error("Failed to create security key file.");
                    throw new RuntimeException("Failed to write security key.");
                }
            } else {
                try {
                    key = new String(Files.readAllBytes(Paths.get(encryptionKey.getAbsolutePath())), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    logger.error("Failed to read security key file.");
                    throw new RuntimeException("Failed to read security key.");
                }
            }
            logger.info("Encryption key set.");
        } else {
            logger.error("MAYPAD_HOME is not set properly.");
            throw new RuntimeException("MAYPAD_HOME is not set properly.");
        }
    }
}