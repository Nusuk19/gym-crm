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
        storage().put(trainer.getUserId(), trainer);

        return trainer;
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
    public Optional<Trainer> findTrainerById(Long id) {
        return Optional.ofNullable(storage().get(id));
    }

    @Override
    public List<Trainer> findAllTrainers() {
        return List.copyOf(storage().values());
    }

    private Map<Long, Trainer> storage() {
        return inMemoryStorage.getTrainerStorage();
    }
}
