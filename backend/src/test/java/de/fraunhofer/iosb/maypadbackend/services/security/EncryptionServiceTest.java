package de.fraunhofer.iosb.maypadbackend.services.security;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EncryptionServiceTest {

    @Autowired
    EncryptionService encryptionService;

    private static final File MAYPAD_HOME = new File("target/maypadhome");

    @ClassRule
    public static final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    /**
     * Setup MaypadHome for testing.
     * @throws Exception if setup fails
     */
    @BeforeClass
    public static void setupMaypadHome() throws Exception {
        if (MAYPAD_HOME.exists()) {
            FileUtils.deleteDirectory(MAYPAD_HOME);
        }
        MAYPAD_HOME.mkdir();
    }

    /**
     * Deletes the created MAYPAD_HOME folder.
     */
    @AfterClass
    public static void cleanupMaypadHome() throws Exception {
        if (MAYPAD_HOME.exists()) {
            FileUtils.deleteDirectory(MAYPAD_HOME);
        }
    }

    @Test
    public void testEncryption() {
        final String password = "12345password54321";
        EncryptedText encryptedText = encryptionService.encrypt(password);
        assertThat(encryptedText).isNotNull();
        assertThat(encryptedText.getSalt()).isNotEqualTo("");
        assertThat(encryptedText.getText()).isNotEqualTo("");

        String decryptedText = encryptionService.decrypt(encryptedText.getText(), encryptedText.getSalt());

        assertThat(decryptedText).isEqualTo(password);

    }

    @Test
    public void testSingleton() {
        final String password = "12345password54321";
        EncryptedText encryptedText = EncryptionService.encryptText(password);
        assertThat(encryptedText).isNotNull();
        assertThat(encryptedText.getSalt()).isNotEqualTo("");
        assertThat(encryptedText.getText()).isNotEqualTo("");

        String decryptedText = EncryptionService.decryptText(encryptedText.getText(), encryptedText.getSalt());

        assertThat(decryptedText).isEqualTo(password);
    }
}
