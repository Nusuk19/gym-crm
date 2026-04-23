package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Training;
import com.gym.crm.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDaoImpl implements TrainingDao {

    private static final Logger log = LoggerFactory.getLogger(TrainingDaoImpl.class);

    private InMemoryStorage inMemoryStorage;

    @Autowired
    public void setInMemoryStorage(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public Training save(Training training) {
        Long id = training.getTrainingId() != null ? training.getTrainingId() : generateId();

        Training trainingWithId = training.toBuilder()
                .trainingId(id)
                .build();

        storage().put(id, trainingWithId);
        log.info("Training saved to storage: id={}", id);

        return trainingWithId;
    }

    @Override
    public Optional<Training> findById(Long id) {
        Optional<Training> result = Optional.ofNullable(storage().get(id));
        if (result.isEmpty()) {
            log.warn("Trainee not found in storage: id={}", id);
        }
        return result;
    }

    @Override
    public List<Training> findAll() {
        return List.copyOf(storage().values());
    }

    private Long generateId() {
        return storage().keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    private Map<Long, Training> storage() {
        return inMemoryStorage.getEntityStorage("trainings");
    }
}
