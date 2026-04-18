package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import com.gym.crm.service.TrainerService;
import com.gym.crm.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {
    private TrainerDao trainerDao;
    private EntityValidator entityValidator;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }

    @Override
    public Trainer create(Trainer trainer) {
        entityValidator.validateTrainer(trainer);

        return trainerDao.save(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        entityValidator.requireValidId(trainer.getUserId());

        return trainerDao.update(trainer);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        entityValidator.requireValidId(id);

        return trainerDao.findById(id);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerDao.findAll();
    }
}
