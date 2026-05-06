package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.UserDao;
import com.gym.crm.dto.request.UserCredentials;
import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.User;
import com.gym.crm.profile.PasswordEncoder;
import com.gym.crm.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private PasswordEncoder passwordEncoder;
    private UserDao userDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void validateTraineeCredentials(UserCredentials credentials) {
        log.info("Validating trainee credentials: username={}", credentials.getUsername());

        Trainee trainee = traineeDao.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + credentials.getUsername()));

        if (!passwordEncoder.matches(credentials.getPassword(), trainee.getUser().getPassword())) {
            log.warn("Authentication failed for trainee: username={}", credentials.getUsername());

            throw new AuthenticationException("Invalid credentials for trainee: " + credentials.getUsername());
        }

        log.info("Trainee credentials validated successfully: username={}", credentials.getUsername());
    }

    @Override
    public void validateTrainerCredentials(UserCredentials credentials) {
        log.info("Validating trainer credentials: username={}", credentials.getUsername());

        Trainer trainer = trainerDao.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + credentials.getUsername()));

        if (!passwordEncoder.matches(credentials.getPassword(), trainer.getUser().getPassword())) {
            log.warn("Authentication failed for trainer: username={}", credentials.getUsername());

            throw new AuthenticationException("Invalid credentials for trainer: " + credentials.getUsername());
        }

        log.info("Trainer credentials validated successfully: username={}", credentials.getUsername());
    }

    @Override
    public void validateCredentials(UserCredentials credentials) {
        log.info("Validating credentials: username={}", credentials.getUsername());

        User user = userDao.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + credentials.getUsername()));

        if (!passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            log.warn("Authentication failed: username={}", credentials.getUsername());

            throw new AuthenticationException("Invalid credentials: " + credentials.getUsername());
        }

        log.info("Credentials validated successfully: username={}", credentials.getUsername());
    }
}