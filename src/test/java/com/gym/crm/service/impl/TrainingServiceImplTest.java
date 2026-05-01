package com.gym.crm.service.impl;

import com.gym.crm.dao.legacy.TrainingDao;
import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.validator.EntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    private static final Long ID = 1L;
    private static final Long NON_EXISTING_ID = 99L;

    private final Training training = buildTraining();

    @Mock
    private TrainingDao trainingDao;
    @Mock
    private EntityValidator validator;
    @InjectMocks
    private TrainingServiceImpl service;

    @Test
    void create_whenValidTraining_savesAndReturnsTraining() {
        when(trainingDao.save(training)).thenReturn(training);

        Training actual = service.create(training);

        assertEquals(training, actual);
        verify(validator).validateTraining(training);
        verify(trainingDao).save(training);
    }

    @Test
    void create_whenValidationFails_throwsExceptionAndDaoNotCalled() {
        doThrow(new EntityValidationException("Training cannot be null"))
                .when(validator).validateTraining(training);

        assertThrows(EntityValidationException.class, () -> service.create(training));

        verify(trainingDao, never()).save(any());
    }

    @Test
    void findById_whenTrainingExists_returnsTraining() {
        when(trainingDao.findById(ID)).thenReturn(Optional.of(training));

        Optional<Training> actual = service.findById(ID);

        assertTrue(actual.isPresent());
        assertEquals(training, actual.get());
        verify(validator).requireValidId(ID);
        verify(trainingDao).findById(ID);
    }

    @Test
    void findById_whenTrainingNotExists_returnsEmpty() {
        when(trainingDao.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<Training> actual = service.findById(NON_EXISTING_ID);

        assertFalse(actual.isPresent());
        verify(validator).requireValidId(NON_EXISTING_ID);
        verify(trainingDao).findById(NON_EXISTING_ID);
    }

    @Test
    void findById_whenInvalidId_throwsExceptionAndDaoNotCalled() {
        doThrow(new EntityValidationException("Id must be a positive integer"))
                .when(validator).requireValidId(any());

        assertThrows(EntityValidationException.class, () -> service.findById(0L));

        verify(trainingDao, never()).findById(any());
    }

    @Test
    void findAll_whenTrainingsExist_returnsAllTrainings() {
        when(trainingDao.findAll()).thenReturn(List.of(training));

        List<Training> actual = service.findAll();

        assertEquals(List.of(training), actual);
        verify(trainingDao).findAll();
    }

    @Test
    void findAll_whenNoTrainingsExist_returnsEmptyList() {
        when(trainingDao.findAll()).thenReturn(List.of());

        List<Training> actual = service.findAll();

        assertTrue(actual.isEmpty());
        verify(trainingDao).findAll();
    }

    private Training buildTraining() {
        return Training.builder()
                .trainingId(ID)
                .traineeId(ID)
                .trainerId(ID)
                .trainingName("Boxing basics")
                .trainingType(new TrainingType("BOXING"))
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(60)
                .build();
    }
}