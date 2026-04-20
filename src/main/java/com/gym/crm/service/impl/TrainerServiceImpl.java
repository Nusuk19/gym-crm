package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.UserProfileService;
import com.gym.crm.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {
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
        validator.validateForUpdate(trainer, trainer.getUserId());

        return trainerDao.update(trainer);
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
