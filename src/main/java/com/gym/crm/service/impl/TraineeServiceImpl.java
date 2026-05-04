package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.User;
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

        log.info("Creating trainee: firstName={}, lastName={}",
                trainee.getUser().getFirstName(), trainee.getUser().getLastName());

        String username = userProfileService.generateUsername(trainee.getUser().getFirstName(), trainee.getUser().getLastName());
        String rawPassword = userProfileService.generatePassword();
        String hashedPassword = userProfileService.hashPassword(rawPassword);

        User userWithProfile = trainee.getUser().toBuilder()
                .username(username)
                .password(hashedPassword)
                .build();

        Trainee traineeWithProfile = trainee.toBuilder()
                .user(userWithProfile)
                .build();

        return traineeDao.save(traineeWithProfile);
    }

    @Override
    public Trainee update(Trainee trainee) {
        String username = trainee.getUser().getUsername();
        log.info("Updating trainee: username={}", username);
        validator.validateTrainee(trainee);

        Trainee existing = traineeDao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));

        User mergedUser = trainee.getUser().toBuilder()
                .id(existing.getUser().getId())
                .username(existing.getUser().getUsername())
                .password(existing.getUser().getPassword())
                .build();

        Trainee merged = trainee.toBuilder()
                .id(existing.getId())
                .user(mergedUser)
                .build();

        return traineeDao.update(merged);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting trainee: id={}", id);
        validator.requireValidId(id);

        traineeDao.deleteById(id);
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