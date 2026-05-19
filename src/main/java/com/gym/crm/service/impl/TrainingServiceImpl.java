package com.gym.crm.service.impl;

import com.gym.crm.annotation.PersistenceTx;
import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.dao.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.dto.request.CreateTrainingRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.common.EntityValidator;
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
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private EntityValidator validator;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setValidator(EntityValidator validator) {
        this.validator = validator;
    }

    @Override
    @PersistenceTx
    public Training create(CreateTrainingRequest request) {
        log.info("Creating training: name={}, traineeUsername={}, trainerUsername={}",
                request.getTrainingName(), request.getTraineeUsername(), request.getTrainerUsername());

        Trainee trainee = traineeDao.findByUsername(request.getTraineeUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + request.getTraineeUsername()));

        Trainer trainer = trainerDao.findByUsername(request.getTrainerUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + request.getTrainerUsername()));

        Training training = Training.builder()
                .name(request.getTrainingName())
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainer.getSpecialization())
                .trainingDate(request.getTrainingDate())
                .trainingDuration(request.getTrainingDuration())
                .build();

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

    @Override
    public List<Training> findByTraineeCriteria(TraineeTrainingSearchFilter filter) {
        validator.requireNonNull(filter, "Search filter cannot be null");
        log.debug("Searching trainings by trainee criteria: {}", filter);

        return trainingDao.findByTraineeCriteria(filter);
    }

    @Override
    public List<Training> findByTrainerCriteria(TrainerTrainingSearchFilter filter) {
        validator.requireNonNull(filter, "Search filter cannot be null");
        log.debug("Searching trainings by trainer criteria: {}", filter);

        return trainingDao.findByTrainerCriteria(filter);
    }
}