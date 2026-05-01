package com.gym.crm.service.impl;

import com.gym.crm.dao.legacy.TrainerDao;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainer;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.UserProfileService;
import com.gym.crm.validator.EntityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerDao trainerDao;
    private EntityValidator validator;
    private UserProfileService userProfileService;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
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
    public Trainer create(Trainer trainer) {
        log.info("Creating trainer: firstName={}, lastName={}", trainer.getFirstName(), trainer.getLastName());
        validator.validateTrainer(trainer);

        String username = userProfileService.generateUsername(
                trainer.getFirstName(), trainer.getLastName());
        String rawPassword = userProfileService.generatePassword();
        String hashedPassword = userProfileService.hashPassword(rawPassword);

        Trainer trainerWithProfile = trainer.toBuilder()
                .username(username)
                .password(hashedPassword)
                .build();

        return trainerDao.save(trainerWithProfile);
    }

    @Override
    public Trainer update(Trainer trainer) {
        log.info("Updating trainer: id={}", trainer.getUserId());
        validator.validateForUpdate(trainer, trainer.getUserId());

        Trainer existing = trainerDao.findById(trainer.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainer.getUserId()));

        Trainer merged = trainer.toBuilder()
                .username(existing.getUsername())
                .password(existing.getPassword())
                .build();

        return trainerDao.update(merged);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        validator.requireValidId(id);

        return trainerDao.findById(id);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerDao.findAll();
    }
}
