package com.gym.crm.service;

import com.gym.crm.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeService {
    Trainee create(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Long id);

    Optional<Trainee> findById(Long id);

    List<Trainee> findAll();
}