package com.gym.crm.dao.legacy.impl;

import com.gym.crm.dao.legacy.TrainerDao;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainer;
import com.gym.crm.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDaoImpl implements TrainerDao {

    private static final Logger log = LoggerFactory.getLogger(TrainerDaoImpl.class);

    private InMemoryStorage inMemoryStorage;

    @Autowired
    public void setInMemoryStorage(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public Trainer save(Trainer trainer) {
        Long id = trainer.getUserId() != null ? trainer.getUserId() : generateId();

        Trainer trainerWithId = trainer.toBuilder()
                .userId(id)
                .build();

        storage().put(id, trainerWithId);
        log.info("Trainer saved to storage: id={}", id);

        return trainerWithId;
    }

    @Override
    public Trainer update(Trainer trainer) {
        if (!storage().containsKey(trainer.getUserId())) {
            throw new EntityNotFoundException("Trainer not found: " + trainer.getUserId());
        }
        storage().put(trainer.getUserId(), trainer);
        log.info("Trainer updated in storage: id={}", trainer.getUserId());

        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        Optional<Trainer> result = Optional.ofNullable(storage().get(id));
        if (result.isEmpty()) {
            log.warn("Trainee not found in storage: id={}", id);
        }
        return result;
    }

    @Override
    public List<Trainer> findAll() {
        return List.copyOf(storage().values());
    }

    private Long generateId() {
        return storage().keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    private Map<Long, Trainer> storage() {
        return inMemoryStorage.getEntityStorage("trainers");
    }
}
