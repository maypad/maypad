package de.fraunhofer.iosb.maypadbackend.services.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EncryptedText {
    private String text;
    private String salt;

    @Override
    public String toString() {
        return text;
    }
}
