package de.fraunhofer.iosb.maypadbackend.services.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class EncryptionServiceTest {

    @Autowired
    EncryptionService encryptionService;

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
