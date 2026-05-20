package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.model.TrainingType;
import com.gym.crm.service.TrainingTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private TrainingTypeDao trainingTypeDao;

    @Autowired
    public void setTrainingTypeDao(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    public List<TrainingType> findAll() {
        log.info("Loading all training types");

        return trainingTypeDao.findAll();
    }
}