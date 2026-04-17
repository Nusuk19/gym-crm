package com.gym.crm.dao;

import com.gym.crm.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {
    Trainer save(Trainer trainer);

    Trainer update(Trainer trainer);

    Optional<Trainer> findTrainerById(Long id);

    List<Trainer> findAllTrainers();
}
