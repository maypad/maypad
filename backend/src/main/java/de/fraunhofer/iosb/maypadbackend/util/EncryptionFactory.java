package de.fraunhofer.iosb.maypadbackend.util;

import de.fraunhofer.iosb.maypadbackend.services.security.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncryptionFactory {

    @Autowired
    private static EncryptionService encryptionService;

    public static EncryptionService getEncryptionService() {
        return encryptionService;
    }

}
