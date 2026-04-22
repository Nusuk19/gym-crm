package com.gym.crm.mapper;

import com.gym.crm.dto.request.CreateTrainingRequest;
import com.gym.crm.dto.response.TrainingResponse;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingMapperTest {

    private static final Long EXISTING_ID = 1L;
    private static final Long TRAINEE_ID = 2L;
    private static final Long TRAINER_ID = 3L;

    private TrainingMapper trainingMapper;

    @BeforeEach
    void setUp() {
        trainingMapper = new TrainingMapper();
    }

    @Test
    void toEntity_fromCreateRequest_mapsAllFieldsCorrectly() {
        CreateTrainingRequest request = buildCreateRequest();

        Training actual = trainingMapper.toEntity(request);

        assertEquals(EXISTING_ID, actual.getTraineeId());
        assertEquals(TRAINEE_ID, actual.getTrainerId());
        assertEquals("Boxing basics", actual.getTrainingName());
        assertEquals("BOXING", actual.getTrainingType().getTrainingTypeName());
        assertEquals(LocalDate.of(2024, 5, 1), actual.getTrainingDate());
        assertEquals(60, actual.getTrainingDuration());
    }

    @Test
    void toEntity_fromCreateRequest_doesNotSetTrainingId() {
        CreateTrainingRequest request = buildCreateRequest();

        Training actual = trainingMapper.toEntity(request);

        assertEquals(null, actual.getTrainingId());
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
                .trainingType(new TrainingType("BOXING"))
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(60)
                .build();
    }

    private Training buildTraining() {
        return Training.builder()
                .trainingId(EXISTING_ID)
                .traineeId(TRAINEE_ID)
                .trainerId(TRAINER_ID)
                .trainingName("Boxing basics")
                .trainingType(new TrainingType("BOXING"))
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(60)
                .build();
    }
}