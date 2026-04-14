package com.gym.crm;

import com.gym.crm.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(AppConfig.class)
class ApplicationContextTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertNotNull(context);
    }
}