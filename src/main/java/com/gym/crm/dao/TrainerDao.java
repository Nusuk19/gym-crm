package com.gym.crm.dao;

import com.gym.crm.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {

    Trainer save(Trainer trainer);

    Trainer update(Trainer trainee);

    Optional<Trainer> findById(Long id);

    Optional<Trainer> findByUsername(String username);

    List<Trainer> findAll();

    List<Trainer> findAllNotAssignedToTrainee(String traineeUsername);
}
