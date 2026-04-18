package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.service.TraineeService;
import com.gym.crm.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public Trainee create(Trainee trainee) {
        Objects.requireNonNull(trainee, "Trainee must not be null");
        ValidationUtils.requireNonBlank(trainee.getFirstName(), "Trainee first name cannot be blank");
        ValidationUtils.requireNonBlank(trainee.getLastName(), "Trainee last name cannot be blank");

        return traineeDao.save(trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        Objects.requireNonNull(trainee, "Trainee cannot be null");
        ValidationUtils.requireValidId(trainee.getUserId());

        return traineeDao.update(trainee);
    }

    @Override
    public void delete(Long id) {
        ValidationUtils.requireValidId(id);

        traineeDao.delete(id);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        ValidationUtils.requireValidId(id);

        return traineeDao.findById(id);
    }

    @Override
    public List<Trainee> findAll() {
        return traineeDao.findAll();
    }
}