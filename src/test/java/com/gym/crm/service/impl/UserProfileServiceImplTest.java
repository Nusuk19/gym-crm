package com.gym.crm.service.impl;

import com.gym.crm.profile.PasswordEncoder;
import com.gym.crm.profile.PasswordGenerator;
import com.gym.crm.profile.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @Test
    void generateUsername_delegatesToUsernameGenerator() {
        when(usernameGenerator.generate("Abdul", "Hariton")).thenReturn("Abdul.Hariton");

        String result = userProfileService.generateUsername("Abdul", "Hariton");

        assertEquals("Abdul.Hariton", result);
        verify(usernameGenerator).generate("Abdul", "Hariton");
    }

    @Test
    void generateUsername_returnsGeneratedUsernameWithSuffix() {
        when(usernameGenerator.generate("Abdul", "Hariton")).thenReturn("Abdul.Hariton1");

        String actual = userProfileService.generateUsername("Abdul", "Hariton");

        assertEquals("Abdul.Hariton1", actual);
        verify(usernameGenerator).generate("Abdul", "Hariton");
    }

    @Test
    void generatePassword_delegatesToPasswordGenerator() {
        when(passwordGenerator.generate()).thenReturn("aB3!xZ9qWm");

        String actual = userProfileService.generatePassword();

        assertEquals("aB3!xZ9qWm", actual);
        verify(passwordGenerator).generate();
    }

    @Test
    void hashPassword_delegatesToPasswordEncoder() {
        when(passwordEncoder.encode("rawPass")).thenReturn("$2a$10$hashedValue");

        String actual = userProfileService.hashPassword("rawPass");

        assertEquals("$2a$10$hashedValue", actual);
        verify(passwordEncoder).encode("rawPass");
    }

    @Test
    void hashPassword_returnsEncodedValue() {
        when(passwordEncoder.encode("anotherPass")).thenReturn("$2a$10$differentHash");

        String actual = userProfileService.hashPassword("anotherPass");

        assertEquals("$2a$10$differentHash", actual);
        verify(passwordEncoder).encode("anotherPass");
    }
}