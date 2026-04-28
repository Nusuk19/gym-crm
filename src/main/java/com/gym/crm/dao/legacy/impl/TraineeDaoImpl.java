package com.gym.crm.dao.legacy.impl;

import com.gym.crm.dao.legacy.TraineeDao;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainee;
import com.gym.crm.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeDaoImpl implements TraineeDao {

    private static final Logger log = LoggerFactory.getLogger(TraineeDaoImpl.class);

    private InMemoryStorage inMemoryStorage;

    @Autowired
    public void setInMemoryStorage(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public Trainee save(Trainee trainee) {
        Long id = trainee.getUserId() != null ? trainee.getUserId() : generateId();

        Trainee traineeWithId = trainee.toBuilder()
                .userId(id)
                .build();

        storage().put(id, traineeWithId);
        log.info("Trainee saved to storage: id={}", id);

        return traineeWithId;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (!storage().containsKey(trainee.getUserId())) {
            throw new EntityNotFoundException("Trainee not found: " + trainee.getUserId());
        }
        storage().put(trainee.getUserId(), trainee);
        log.info("Trainee updated in storage: id={}", trainee.getUserId());

        return trainee;
    }

    @Override
    public void delete(Long userId) {
        if (!storage().containsKey(userId)) {
            throw new EntityNotFoundException("Trainee not found with id: " + userId);
        }
        storage().remove(userId);
        log.info("Trainee removed from storage: id={}", userId);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        Optional<Trainee> result = Optional.ofNullable(storage().get(id));
        if (result.isEmpty()) {
            log.warn("Trainee not found in storage: id={}", id);
        }

        return result;
    }

    @Override
    public List<Trainee> findAll() {
        return List.copyOf(storage().values());
    }

    private Long generateId() {
        return storage().keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    private Map<Long, Trainee> storage() {
        return inMemoryStorage.getEntityStorage("trainees");
    }
}
