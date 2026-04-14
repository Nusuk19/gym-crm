package com.gym.crm;

import com.gym.crm.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GymCrmApplication {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            System.out.println("Spring context loaded successfully");
        }
    }
}
