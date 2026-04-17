package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainee;
import com.gym.crm.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeDaoImpl implements TraineeDao {

    private InMemoryStorage inMemoryStorage;

    @Autowired
    public void setInMemoryStorage(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public Trainee save(Trainee trainee) {
        storage().put(trainee.getUserId(), trainee);

        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (!storage().containsKey(trainee.getUserId())) {
            throw new EntityNotFoundException("Trainee not found: " + trainee.getUserId());
        }
        storage().put(trainee.getUserId(), trainee);

        return trainee;
    }

    @Override
    public void delete(Long userId) {
        storage().remove(userId);
    }

    @Override
    public Optional<Trainee> findTraineeById(Long id) {
        return Optional.ofNullable(storage().get(id));
    }

    @Override
    public List<Trainee> findAllTrainees() {
        return List.copyOf(storage().values());
    }

    private Map<Long, Trainee> storage() {
        return inMemoryStorage.getTraineeStorage();
    }
}
