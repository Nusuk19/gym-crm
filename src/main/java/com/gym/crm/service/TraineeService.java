package com.gym.crm.service;

import com.gym.crm.dto.request.ActivationRequest;
import com.gym.crm.dto.request.ChangePasswordRequest;
import com.gym.crm.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeService {
    Trainee create(Trainee trainee);

    Trainee update(Trainee trainee);

    void updateTrainers(String traineeUsername, List<String> trainerUsernames);

    void deleteById(Long id);

    void deleteByUsername(String username);

    Optional<Trainee> findById(Long id);

    Optional<Trainee> findByUsername(String username);

    List<Trainee> findAll();

    void changePassword(ChangePasswordRequest request);

    void activate(ActivationRequest request);

    void deactivate(ActivationRequest request);
}