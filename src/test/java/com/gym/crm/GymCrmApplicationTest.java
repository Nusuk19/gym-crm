package com.gym.crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
class GymCrmApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void main_shouldNotThrow() {
        assertDoesNotThrow(() -> GymCrmApplication.main(new String[]{}));
    }
}