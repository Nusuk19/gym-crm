package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dto.request.ActivationRequest;
import com.gym.crm.dto.request.ChangePasswordRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.User;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.UserProfileService;
import com.gym.crm.service.UserService;
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
    private UserService userService;

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

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
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
    public void updateTrainers(String traineeUsername, List<String> trainerUsernames) {
        validator.requireNonBlank(traineeUsername, "Username cannot be blank");
        validator.requireNonNull(trainerUsernames, "Trainer usernames list cannot be null");

        log.info("Updating trainers list for trainee: username={}, trainers={}", traineeUsername, trainerUsernames);

        if (traineeDao.findByUsername(traineeUsername).isEmpty()) {
            throw new EntityNotFoundException("Trainee not found: " + traineeUsername);
        }

        traineeDao.updateTrainers(traineeUsername, trainerUsernames);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting trainee: id={}", id);
        validator.requireValidId(id);

        traineeDao.deleteById(id);
    }

    @Override
    public void deleteByUsername(String username) {
        log.info("Deleting trainee: username={}", username);
        validator.requireNonBlank(username, "Username cannot be blank");

        traineeDao.deleteByUsername(username);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        validator.requireValidId(id);

        return traineeDao.findById(id);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        validator.requireNonBlank(username, "Username cannot be blank");
        log.debug("Looking up trainee by username={}", username);

        return traineeDao.findByUsername(username);
    }

    @Override
    public List<Trainee> findAll() {
        return traineeDao.findAll();
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        requireTraineeByUsername(request.getUsername());

        userService.changePassword(request);
    }

    @Override
    public void activate(ActivationRequest request) {
        requireTraineeByUsername(request.getUsername());

        userService.activate(request);
    }

    @Override
    public void deactivate(ActivationRequest request) {
        requireTraineeByUsername(request.getUsername());

        userService.deactivate(request);
    }

    private Trainee requireTraineeByUsername(String username) {
        validator.requireNonBlank(username, "Username cannot be blank");

        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
    }
}