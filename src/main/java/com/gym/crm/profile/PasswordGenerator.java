package com.gym.crm.profile;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGenerator {
    private static final int REQUIRED_PASSWORD_LENGTH = 10;

    private final SecureRandom random = new SecureRandom();

    @Value("${password.characters}")
    private String characters;

    @Value("${password.length}")
    private int passwordLength;

    @PostConstruct
    public void validate() {
        if (characters == null || characters.isEmpty()) {
            throw new IllegalStateException("Password characters must not be empty");
        }
        if (passwordLength != REQUIRED_PASSWORD_LENGTH) {
            throw new IllegalStateException(
                    String.format("Default password length value must be exactly %d, but is defined in properties as %d",
                            REQUIRED_PASSWORD_LENGTH, passwordLength));
        }
    }

    public String generate() {
        StringBuilder password = new StringBuilder(passwordLength);
        for (int i = 0; i < passwordLength; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }
}
