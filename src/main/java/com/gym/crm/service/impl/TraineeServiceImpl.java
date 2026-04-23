package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.UserProfileService;
import com.gym.crm.validator.EntityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDao traineeDao;
    private EntityValidator validator;
    private UserProfileService userProfileService;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setValidator(EntityValidator validator) {
        this.validator = validator;
    }

    @Autowired
    public void setUserProfileService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public Trainee create(Trainee trainee) {
        validator.validateTrainee(trainee);

        String username = userProfileService.generateUsername(
                trainee.getFirstName(), trainee.getLastName());
        String rawPassword = userProfileService.generatePassword();
        String hashedPassword = userProfileService.hashPassword(rawPassword);

        Trainee traineeWithProfile = trainee.toBuilder()
                .username(username)
                .password(hashedPassword)
                .build();

        log.info("Creating trainee: firstName={}, lastName={}", trainee.getFirstName(), trainee.getLastName());

        return traineeDao.save(traineeWithProfile);
    }

    @Override
    public Trainee update(Trainee trainee) {
        log.info("Updating trainee: id={}", trainee.getUserId());
        validator.validateForUpdate(trainee, trainee.getUserId());

        return traineeDao.update(trainee);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting trainee: id={}", id);
        validator.requireValidId(id);

        traineeDao.delete(id);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        validator.requireValidId(id);

        return traineeDao.findById(id);
    }

    @Override
    public List<Trainee> findAll() {
        return traineeDao.findAll();
    }
}