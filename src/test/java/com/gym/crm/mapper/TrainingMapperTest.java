package com.gym.crm.mapper;

import com.gym.crm.dto.request.CreateTrainingRequest;
import com.gym.crm.dto.response.TrainingResponse;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingMapperTest {

    private static final Long EXISTING_ID = 1L;
    private static final Long TRAINEE_ID = 2L;
    private static final Long TRAINER_ID = 3L;

    private TrainingMapper trainingMapper;

    @BeforeEach
    void setUp() {
        trainingMapper = Mappers.getMapper(TrainingMapper.class);
    }
    @Test
    void toEntity_fromCreateRequest_mapsAllFieldsCorrectly() {
        CreateTrainingRequest request = buildCreateRequest();

        Training actual = trainingMapper.toEntity(request);

        assertEquals(EXISTING_ID, actual.getTrainee().getId());
        assertEquals(TRAINEE_ID, actual.getTrainer().getId());
        assertEquals("Boxing basics", actual.getName());
        assertEquals("BOXING", actual.getTrainingType().getTrainingTypeName());
        assertEquals(LocalDate.of(2024, 5, 1), actual.getTrainingDate());
        assertEquals(BigDecimal.valueOf(60), actual.getTrainingDuration());
    }

    @Test
    void toEntity_fromCreateRequest_doesNotSetTrainingId() {
        CreateTrainingRequest request = buildCreateRequest();

        Training actual = trainingMapper.toEntity(request);

        assertEquals(null, actual.getId());
    }

    @Test
    void toResponse_fromTraining_mapsAllFieldsCorrectly() {
        Training training = buildTraining();

        TrainingResponse actual = trainingMapper.toResponse(training);

        assertEquals(1L, actual.getId());
        assertEquals(TRAINEE_ID, actual.getTraineeId());
        assertEquals(TRAINER_ID, actual.getTrainerId());
        assertEquals("Boxing basics", actual.getTrainingName());
        assertEquals("BOXING", actual.getTrainingType().getTrainingTypeName());
        assertEquals(LocalDate.of(2024, 5, 1), actual.getTrainingDate());
        assertEquals(60, actual.getTrainingDuration());
    }

    private CreateTrainingRequest buildCreateRequest() {
        return CreateTrainingRequest.builder()
                .traineeId(EXISTING_ID)
                .trainerId(TRAINEE_ID)
                .trainingName("Boxing basics")
                .trainingType(TrainingType.builder().trainingTypeName("BOXING").build())
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(60)
                .build();
    }

    private Training buildTraining() {
        User user = User.builder().build();

        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .build();

        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .user(user)
                .specialization(TrainingType.builder().trainingTypeName("BOXING").build())
                .build();

        return Training.builder()
                .id(EXISTING_ID)
                .name("Boxing basics")
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(TrainingType.builder().trainingTypeName("BOXING").build())
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(BigDecimal.valueOf(60))
                .build();
    }
}