package com.gym.crm.service.impl;

import com.gym.crm.dao.legacy.TrainingDao;
import com.gym.crm.model.Training;
import com.gym.crm.service.TrainingService;
import com.gym.crm.validator.EntityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingDao trainingDao;
    private EntityValidator validator;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setValidator(EntityValidator validator) {
        this.validator = validator;
    }

    @Override
    public Training create(Training training) {
        log.info("Creating training: name={}, traineeId={}, trainerId={}",
                training.getTrainingName(), training.getTraineeId(), training.getTrainerId());
        validator.validateTraining(training);

        return trainingDao.save(training);
    }

    @Override
    public Optional<Training> findById(Long id) {
        validator.requireValidId(id);

        return trainingDao.findById(id);
    }

    @Override
    public List<Training> findAll() {
        return trainingDao.findAll();
    }
}