package com.gym.crm.dao;

import com.gym.crm.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {

    Trainee save(Trainee trainee);

    Trainee update(Trainee trainee);

    void updateTrainers(String traineeUsername, List<String> trainerUsernames);

    void deleteById(Long id);

    void deleteByUsername(String username);

    Optional<Trainee> findById(Long id);

    Optional<Trainee> findByUsername(String username);

    List<Trainee> findAll();
}
