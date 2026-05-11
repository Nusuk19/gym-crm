package com.gym.crm;

import com.gym.crm.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class GymCrmApplication {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            log.info("Spring context loaded successfully");
        }
    }
}
