package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainer;
import com.gym.crm.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDaoImpl implements TrainerDao {

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

        return trainerWithId;
    }

    @Override
    public Trainer update(Trainer trainer) {
        if (!storage().containsKey(trainer.getUserId())) {
            throw new EntityNotFoundException("Trainer not found: " + trainer.getUserId());
        }
        storage().put(trainer.getUserId(), trainer);

        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(storage().get(id));
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
        return inMemoryStorage.getTrainerStorage();
    }
}
