package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.dao.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.common.EntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
    private static final String TRAINEE_USERNAME = "John.Doe";
    private static final String TRAINER_USERNAME = "Mike.Tyson";

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

    @Test
    void findByTraineeCriteria_whenFilterValid_returnsMatchingTrainings() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(TRAINEE_USERNAME)
                .build();
        when(trainingDao.findByTraineeCriteria(filter)).thenReturn(List.of(training));

        List<Training> actual = service.findByTraineeCriteria(filter);

        assertEquals(1, actual.size());
        assertEquals(training, actual.get(0));
        verify(validator).requireNonNull(filter, "Search filter cannot be null");
        verify(trainingDao).findByTraineeCriteria(filter);
    }

    @Test
    void findByTraineeCriteria_whenNoResults_returnsEmptyList() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(TRAINEE_USERNAME)
                .fromDate(LocalDate.of(2030, 1, 1))
                .build();
        when(trainingDao.findByTraineeCriteria(filter)).thenReturn(List.of());

        List<Training> actual = service.findByTraineeCriteria(filter);

        assertTrue(actual.isEmpty());
        verify(trainingDao).findByTraineeCriteria(filter);
    }

    @Test
    void findByTrainerCriteria_whenFilterValid_returnsMatchingTrainings() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(TRAINER_USERNAME)
                .build();
        when(trainingDao.findByTrainerCriteria(filter)).thenReturn(List.of(training));

        List<Training> actual = service.findByTrainerCriteria(filter);

        assertEquals(1, actual.size());
        assertEquals(training, actual.get(0));
        verify(validator).requireNonNull(filter, "Search filter cannot be null");
        verify(trainingDao).findByTrainerCriteria(filter);
    }

    @Test
    void findByTrainerCriteria_whenNoResults_returnsEmptyList() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(TRAINER_USERNAME)
                .fromDate(LocalDate.of(2030, 1, 1))
                .build();
        when(trainingDao.findByTrainerCriteria(filter)).thenReturn(List.of());

        List<Training> actual = service.findByTrainerCriteria(filter);

        assertTrue(actual.isEmpty());
        verify(trainingDao).findByTrainerCriteria(filter);
    }

    @Test
    void findByTrainerCriteria_whenFilterNull_throwsEntityValidationException() {
        doThrow(new EntityValidationException("Search filter cannot be null"))
                .when(validator).requireNonNull(null, "Search filter cannot be null");

        assertThrows(EntityValidationException.class, () -> service.findByTrainerCriteria(null));

        verify(trainingDao, never()).findByTrainerCriteria(any());
    }


    @Test
    void findByTraineeCriteria_whenFilterNull_throwsEntityValidationException() {
        doThrow(new EntityValidationException("Search filter cannot be null"))
                .when(validator).requireNonNull(null, "Search filter cannot be null");

        assertThrows(EntityValidationException.class, () -> service.findByTraineeCriteria(null));

        verify(trainingDao, never()).findByTraineeCriteria(any());
    }


    private Training buildTraining() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .build();

        Trainee trainee = Trainee.builder()
                .user(user)
                .build();

        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(TrainingType.builder()
                        .trainingTypeName("BOXING")
                        .build())
                .build();

        return Training.builder()
                .id(ID)
                .name("Boxing basics")
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(TrainingType.builder().trainingTypeName("BOXING").build())
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(BigDecimal.valueOf(60))
                .build();
    }
}