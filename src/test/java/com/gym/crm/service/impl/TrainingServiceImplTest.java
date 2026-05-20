package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.dao.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.dto.request.CreateTrainingRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.common.EntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private EntityValidator validator;
    @InjectMocks
    private TrainingServiceImpl service;

    @Test
    void create_whenValidRequest_buildsTrainingAndSaves() {
        Trainee trainee = buildTrainee();
        Trainer trainer = buildTrainer();
        CreateTrainingRequest request = buildCreateRequest();

        when(traineeDao.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.of(trainer));
        when(trainingDao.save(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        Training actual = service.create(request);

        assertThat(actual.getName()).isEqualTo("Boxing basics");
        assertThat(actual.getTrainee()).isEqualTo(trainee);
        assertThat(actual.getTrainer()).isEqualTo(trainer);
        assertThat(actual.getTrainingType()).isEqualTo(trainer.getSpecialization());
        assertThat(actual.getTrainingDate()).isEqualTo(LocalDate.of(2024, 5, 1));
        assertThat(actual.getTrainingDuration()).isEqualByComparingTo(BigDecimal.valueOf(60));

        verify(traineeDao).findByUsername(TRAINEE_USERNAME);
        verify(trainerDao).findByUsername(TRAINER_USERNAME);
        verify(validator).validateTraining(any(Training.class));
        verify(trainingDao).save(any(Training.class));
    }

    @Test
    void create_whenValidationFails_throwsExceptionAndDaoNotCalled() {
        CreateTrainingRequest request = buildCreateRequest();

        when(traineeDao.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Trainee not found")
                .hasMessageContaining(TRAINEE_USERNAME);

        verify(trainerDao, never()).findByUsername(any());
        verify(trainingDao, never()).save(any());
    }

    @Test
    void create_whenTrainerNotFound_throwsEntityNotFoundException() {
        CreateTrainingRequest request = buildCreateRequest();

        when(traineeDao.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.of(buildTrainee()));
        when(trainerDao.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Trainer not found")
                .hasMessageContaining(TRAINER_USERNAME);

        verify(trainingDao, never()).save(any());
    }

    @Test
    void create_usesTrainerSpecializationAsTrainingType() {
        Trainer trainer = buildTrainer();
        CreateTrainingRequest request = buildCreateRequest();

        when(traineeDao.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.of(buildTrainee()));
        when(trainerDao.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.of(trainer));
        when(trainingDao.save(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        Training actual = service.create(request);

        assertThat(actual.getTrainingType()).isEqualTo(trainer.getSpecialization());

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDao).save(captor.capture());
        assertThat(captor.getValue().getTrainingType().getTrainingTypeName()).isEqualTo("BOXING");
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

    private CreateTrainingRequest buildCreateRequest() {
        return CreateTrainingRequest.builder()
                .traineeUsername(TRAINEE_USERNAME)
                .trainerUsername(TRAINER_USERNAME)
                .trainingName("Boxing basics")
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(BigDecimal.valueOf(60))
                .build();
    }

    private Trainee buildTrainee() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username(TRAINEE_USERNAME)
                .build();
        return Trainee.builder()
                .user(user)
                .build();
    }

    private Trainer buildTrainer() {
        User user = User.builder()
                .firstName("Mike")
                .lastName("Tyson")
                .username(TRAINER_USERNAME)
                .build();
        return Trainer.builder()
                .user(user)
                .specialization(TrainingType.builder()
                        .trainingTypeName("BOXING")
                        .build())
                .build();
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