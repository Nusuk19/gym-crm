package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Training;
import com.gym.crm.service.TrainingService;
import com.gym.crm.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {
    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public Training create(Training training) {
        Objects.requireNonNull(training, "Training cannot be null");
        ValidationUtils.requireNonBlank(training.getTrainingName(), "Training name cannot be blank");
        ValidationUtils.requireValidId(training.getTraineeId());
        ValidationUtils.requireValidId(training.getTrainerId());
        Objects.requireNonNull(training.getTrainingDate(), "Training date cannot be null");
        Objects.requireNonNull(training.getTrainingType(), "Training type cannot be null");

        return trainingDao.save(training);
    }

    @Override
    public Optional<Training> findById(Long id) {
        ValidationUtils.requireValidId(id);

        return trainingDao.findById(id);
    }

    @Override
    public List<Training> findAll() {
        return trainingDao.findAll();
    }
}