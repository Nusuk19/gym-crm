package com.gym.crm.service;

import com.gym.crm.dao.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.dao.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingService {
    Training create(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();

    List<Training> findByTraineeCriteria(TraineeTrainingSearchFilter filter);

    List<Training> findByTrainerCriteria(TrainerTrainingSearchFilter filter);
}