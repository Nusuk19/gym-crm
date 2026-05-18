package com.gym.crm.service.impl;

import com.gym.crm.annotation.PersistenceTx;
import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.dto.request.ActivationRequest;
import com.gym.crm.dto.request.ChangePasswordRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.UserProfileService;
import com.gym.crm.service.UserService;
import com.gym.crm.service.common.EntityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private static final String USERNAME_BLANK_MSG = "Username cannot be blank";

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private TrainingTypeDao trainingTypeDao;
    private EntityValidator validator;
    private UserProfileService userProfileService;
    private UserService userService;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainingTypeDao(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
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
    @PersistenceTx
    public Trainer create(Trainer trainer, String specializationName) {
        log.info("Creating trainer: firstName={}, lastName={}",
                trainer.getUser().getFirstName(), trainer.getUser().getLastName());
        validator.validateTrainer(trainer);

        TrainingType specialization = resolveSpecialization(specializationName);

        String username = userProfileService.generateUsername(
                trainer.getUser().getFirstName(), trainer.getUser().getLastName());
        String rawPassword = userProfileService.generatePassword();
        String hashedPassword = userProfileService.hashPassword(rawPassword);

        User userWithProfile = trainer.getUser().toBuilder()
                .username(username)
                .password(hashedPassword)
                .rawPassword(rawPassword)
                .isActive(Boolean.TRUE)
                .build();

        Trainer trainerWithProfile = trainer.toBuilder()
                .user(userWithProfile)
                .specialization(specialization)
                .build();

        validator.validateTrainer(trainerWithProfile);

        return trainerDao.save(trainerWithProfile);
    }

    @Override
    @PersistenceTx
    public Trainer update(Trainer trainer) {
        String username = trainer.getUser().getUsername();
        log.info("Updating trainer: username={}", username);
        validator.validateTrainer(trainer);

        Trainer existing = trainerDao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));

        User mergedUser = trainer.getUser().toBuilder()
                .id(existing.getUser().getId())
                .username(existing.getUser().getUsername())
                .password(existing.getUser().getPassword())
                .build();

        Trainer merged = trainer.toBuilder()
                .id(existing.getId())
                .user(mergedUser)
                .specialization(existing.getSpecialization())
                .build();

        return trainerDao.update(merged);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        validator.requireValidId(id);

        return trainerDao.findById(id);
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        validator.requireNonBlank(username, USERNAME_BLANK_MSG);
        log.debug("Looking up trainer by username={}", username);

        return trainerDao.findByUsername(username);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerDao.findAll();
    }

    @Override
    @PersistenceTx
    public List<Trainer> findAllNotAssignedToTrainee(String traineeUsername) {
        validator.requireNonBlank(traineeUsername, USERNAME_BLANK_MSG);

        traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + traineeUsername));

        return trainerDao.findAllNotAssignedToTrainee(traineeUsername);
    }

    @Override
    @PersistenceTx
    public void changePassword(ChangePasswordRequest request) {
        requireTrainerByUsername(request.getUsername());

        userService.changePassword(request);
    }

    @Override
    @PersistenceTx
    public void activate(ActivationRequest request) {
        requireTrainerByUsername(request.getUsername());

        userService.activate(request);
    }

    @Override
    @PersistenceTx
    public void deactivate(ActivationRequest request) {
        requireTrainerByUsername(request.getUsername());

        userService.deactivate(request);
    }

    private TrainingType resolveSpecialization(String name) {
        validator.requireNonBlank(name, "Specialization name cannot be blank");

        return trainingTypeDao.findByTrainingTypeName(name)
                .orElseThrow(() -> new EntityNotFoundException("Specialization not found: " + name));
    }

    private Trainer requireTrainerByUsername(String username) {
        validator.requireNonBlank(username, USERNAME_BLANK_MSG);

        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
    }
}
