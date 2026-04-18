package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import com.gym.crm.service.TrainerService;
import com.gym.crm.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {
    private TrainerDao trainerDao;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public Trainer create(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer cannot be null");
        ValidationUtils.requireNonBlank(trainer.getFirstName(), "Trainer first name cannot be blank");
        ValidationUtils.requireNonBlank(trainer.getLastName(), "Trainer last name cannot be blank");

        return trainerDao.save(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer cannot be null");
        ValidationUtils.requireValidId(trainer.getUserId());

        return trainerDao.update(trainer);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        ValidationUtils.requireValidId(id);

        return trainerDao.findById(id);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerDao.findAll();
    }
}
