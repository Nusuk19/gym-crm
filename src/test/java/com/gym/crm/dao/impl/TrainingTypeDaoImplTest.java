package com.gym.crm.dao.impl;

import com.gym.crm.dao.AbstractRepositoryTest;
import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingTypeDaoImplTest extends AbstractRepositoryTest<TrainingTypeDao> {

    @Test
    void findById_existing_returnsTrainingType() {
        TrainingType boxing = dao.findByTrainingTypeName("Boxing").orElseThrow();

        Optional<TrainingType> result = dao.findById(boxing.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(boxing.getId());
        assertThat(result.get().getTrainingTypeName()).isEqualTo(boxing.getTrainingTypeName());
    }

    @Test
    void findById_nonExisting_returnsEmpty() {
        Optional<TrainingType> result = dao.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByTrainingTypeName_existing_returnsEntity() {
        Optional<TrainingType> result = dao.findByTrainingTypeName("Boxing");

        assertThat(result.get().getTrainingTypeName()).isEqualTo("Boxing");
        assertThat(result.get().getId()).isNotNull();
    }

    @Test
    void findByTrainingTypeName_nonExisting_returnsEmpty() {
        Optional<TrainingType> result = dao.findByTrainingTypeName("Unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsAllTrainingTypes() {
        List<TrainingType> result = dao.findAll();

        assertThat(result)
                .isNotEmpty()
                .extracting(TrainingType::getTrainingTypeName)
                .contains("Boxing", "Cardio", "Yoga", "Fitness", "Pilates");
    }

    @Override
    protected Class<TrainingTypeDao> getDaoClass() {
        return TrainingTypeDao.class;
    }
}