package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.service.TraineeService;
import com.gym.crm.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;
    private EntityValidator entityValidator;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }

    @Override
    public Trainee create(Trainee trainee) {
        entityValidator.validateTrainee(trainee);

        return traineeDao.save(trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        entityValidator.requireValidId(trainee.getUserId());

        return traineeDao.update(trainee);
    }

    @Override
    public void delete(Long id) {
        entityValidator.requireValidId(id);

        traineeDao.delete(id);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        entityValidator.requireValidId(id);

        return traineeDao.findById(id);
    }

    @Override
    public List<Trainee> findAll() {
        return traineeDao.findAll();
    }
}