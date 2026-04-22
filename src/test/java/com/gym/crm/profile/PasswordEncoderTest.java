package com.gym.crm.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderTest {

    private static final String RAW_PASSWORD = "rawPassword";

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new PasswordEncoder();
    }

    @Test
    void encode_returnsNonNullHash() {
        String actual = passwordEncoder.encode(RAW_PASSWORD);

        assertNotNull(actual);
    }

    @Test
    void encode_returnsDifferentValueThanRawPassword() {
        String actual = passwordEncoder.encode(RAW_PASSWORD);

        assertNotEquals(RAW_PASSWORD, actual);
    }

    @Test
    void encode_returnsBCryptFormattedHash() {
        String actual = passwordEncoder.encode(RAW_PASSWORD);

        assertTrue(actual.startsWith("$2a$"), "Expected BCrypt hash starting with $2a$, but got: " + actual);
    }

    @Test
    void encode_samePasswordProducesDifferentHashes() {
        String firstHash = passwordEncoder.encode(RAW_PASSWORD);
        String secondHash = passwordEncoder.encode(RAW_PASSWORD);

        assertNotEquals(firstHash, secondHash);
    }
}