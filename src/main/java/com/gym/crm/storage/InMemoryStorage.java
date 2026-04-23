package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InMemoryStorage {

    private Map<String, Map<Long, Object>> storage;
    private StorageInitializer initializer;

    @Autowired
    public void setStorage(Map<String, Map<Long, Object>> storage) {
        this.storage = storage;
    }

    @Autowired
    public void setInitializer(StorageInitializer initializer) {
        this.initializer = initializer;
    }

    @PostConstruct
    public void init() {
        this.<Trainee>getEntityStorage("trainees").putAll(initializer.loadTrainees());
        this.<Trainer>getEntityStorage("trainers").putAll(initializer.loadTrainers());
        this.<Training>getEntityStorage("trainings").putAll(initializer.loadTrainings());
    }

    @SuppressWarnings("unchecked")
    public <T> Map<Long, T> getEntityStorage(String namespace) {
        return (Map<Long, T>) (Map<Long, ?>) storage.get(namespace);
    }
}