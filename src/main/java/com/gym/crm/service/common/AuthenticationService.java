package com.gym.crm.service.common;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.UserDao;
import com.gym.crm.dto.request.UserCredentials;
import com.gym.crm.exception.AuthenticationFailedException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.User;
import com.gym.crm.service.profile.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationService {

    private static final String INVALID_CREDENTIALS = "Invalid credentials";

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

    public void validateTraineeCredentials(UserCredentials credentials) {
        log.info("Validating trainee credentials: username={}", credentials.getUsername());

        Trainee trainee = traineeDao.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(credentials.getPassword(), trainee.getUser().getPassword())) {
            log.warn("Authentication failed for trainee: username={}", credentials.getUsername());

            throw new AuthenticationFailedException(INVALID_CREDENTIALS);
        }

        log.info("Trainee credentials validated successfully: username={}", credentials.getUsername());
    }

    public void validateTrainerCredentials(UserCredentials credentials) {
        log.info("Validating trainer credentials: username={}", credentials.getUsername());

        Trainer trainer = trainerDao.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(credentials.getPassword(), trainer.getUser().getPassword())) {
            log.warn("Authentication failed for trainer: username={}", credentials.getUsername());

            throw new AuthenticationFailedException(INVALID_CREDENTIALS);
        }

        log.info("Trainer credentials validated successfully: username={}", credentials.getUsername());
    }

    public void validateCredentials(UserCredentials credentials) {
        log.info("Validating credentials: username={}", credentials.getUsername());

        User user = userDao.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_CREDENTIALS));


        if (!passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            log.warn("Authentication failed: username={}", credentials.getUsername());

            throw new AuthenticationFailedException(INVALID_CREDENTIALS);
        }

        log.info("Credentials validated successfully: username={}", credentials.getUsername());
    }
}