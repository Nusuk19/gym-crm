package com.gym.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StorageConfig {

    @Bean
    public Map<String, Map<Long, Object>> storage() {
        Map<String, Map<Long, Object>> storage = new HashMap<>();
        storage.put("trainees", new HashMap<>());
        storage.put("trainers", new HashMap<>());
        storage.put("trainings", new HashMap<>());
        return storage;
    }
}