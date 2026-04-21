package com.gym.crm.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    private static final int PASSWORD_LENGTH = 10;
    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";

    private PasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new PasswordGenerator();
        ReflectionTestUtils.setField(passwordGenerator, "characters", ALLOWED_CHARACTERS);
        ReflectionTestUtils.setField(passwordGenerator, "passwordLength", PASSWORD_LENGTH);
        passwordGenerator.validate();
    }

    @Test
    void generate_returnsNonNullPassword() {
        String actual = passwordGenerator.generate();

        assertNotNull(actual);
    }

    @Test
    void generate_returnsPasswordOfCorrectLength() {
        String actual = passwordGenerator.generate();

        assertEquals(PASSWORD_LENGTH, actual.length());
    }

    @Test
    void generate_returnsDifferentPasswordsEachTime() {
        String first = passwordGenerator.generate();
        String second = passwordGenerator.generate();

        assertNotEquals(first, second);
    }

    @Test
    void generate_passwordContainsOnlyAllowedCharacters() {
        String actual = passwordGenerator.generate();

        for (char c : actual.toCharArray()) {
            assertTrue(ALLOWED_CHARACTERS.indexOf(c) >= 0,
                    "Character '" + c + "' is not in allowed characters");
        }
    }

    @Test
    void validate_whenCharactersIsEmpty_throwsIllegalStateException() {
        ReflectionTestUtils.setField(passwordGenerator, "characters", "");

        assertThrows(IllegalStateException.class, () -> passwordGenerator.validate());
    }

    @Test
    void validate_whenCharactersIsNull_throwsIllegalStateException() {
        ReflectionTestUtils.setField(passwordGenerator, "characters", null);

        assertThrows(IllegalStateException.class, () -> passwordGenerator.validate());
    }

    @Test
    void validate_whenPasswordLengthIsWrong_throwsIllegalStateException() {
        ReflectionTestUtils.setField(passwordGenerator, "passwordLength", 5);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> passwordGenerator.validate());

        assertTrue(ex.getMessage().contains("10"));
        assertTrue(ex.getMessage().contains("5"));
    }

    @Test
    void validate_whenPasswordLengthIsCorrect_doesNotThrow() {
        passwordGenerator.validate();
    }
}