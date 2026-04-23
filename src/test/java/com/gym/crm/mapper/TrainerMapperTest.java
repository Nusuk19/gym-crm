package com.gym.crm.mapper;

import com.gym.crm.dto.request.CreateTrainerRequest;
import com.gym.crm.dto.request.UpdateTrainerRequest;
import com.gym.crm.dto.response.TrainerResponse;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerMapperTest {

    private static final Long EXISTING_ID = 1L;

    private TrainerMapper trainerMapper;

    @BeforeEach
    void setUp() {
        trainerMapper = new TrainerMapper();
    }

    @Test
    void toEntity_fromCreateRequest_mapsAllFieldsCorrectly() {
        CreateTrainerRequest request = buildCreateRequest();

        Trainer actual = trainerMapper.toEntity(request);

        assertEquals("Mike", actual.getFirstName());
        assertEquals("Tyson", actual.getLastName());
        assertEquals("BOXING", actual.getSpecialization().getTrainingTypeName());
        assertTrue(actual.isActive());
    }

    @Test
    void toEntity_fromCreateRequest_doesNotSetIdUsernameOrPassword() {
        CreateTrainerRequest request = CreateTrainerRequest.builder()
                .firstName("Mike")
                .lastName("Tyson")
                .specialization(new TrainingType("BOXING"))
                .build();

        Trainer actual = trainerMapper.toEntity(request);

        assertNull(actual.getUserId());
        assertNull(actual.getUsername());
        assertNull(actual.getPassword());
    }

    @Test
    void toEntity_fromUpdateRequest_mapsAllFieldsCorrectly() {
        UpdateTrainerRequest request = buildUpdateRequest();

        Trainer actual = trainerMapper.toEntity(request);

        assertEquals(EXISTING_ID, actual.getUserId());
        assertEquals("Mike", actual.getFirstName());
        assertEquals("Tyson", actual.getLastName());
        assertEquals("YOGA", actual.getSpecialization().getTrainingTypeName());
        assertFalse(actual.isActive());
    }

    @Test
    void toResponse_fromTrainer_mapsAllFieldsCorrectly() {
        Trainer trainer = buildTrainer();

        TrainerResponse actual = trainerMapper.toResponse(trainer);

        assertEquals(EXISTING_ID, actual.getId());
        assertEquals("Mike", actual.getFirstName());
        assertEquals("Tyson", actual.getLastName());
        assertEquals("Mike.Tyson", actual.getUsername());
        assertEquals("BOXING", actual.getSpecialization().getTrainingTypeName());
        assertTrue(actual.isActive());
    }

    @Test
    void toResponse_passwordFieldNotPresentInResponse() {
        assertThrows(NoSuchFieldException.class,
                () -> TrainerResponse.class.getDeclaredField("password"));
    }

    private CreateTrainerRequest buildCreateRequest() {
        return CreateTrainerRequest.builder()
                .firstName("Mike")
                .lastName("Tyson")
                .specialization(new TrainingType("BOXING"))
                .isActive(true)
                .build();
    }

    private UpdateTrainerRequest buildUpdateRequest() {
        return UpdateTrainerRequest.builder()
                .id(EXISTING_ID)
                .firstName("Mike")
                .lastName("Tyson")
                .specialization(new TrainingType("YOGA"))
                .isActive(false)
                .username("Mike.Tyson")
                .build();
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .userId(EXISTING_ID)
                .firstName("Mike")
                .lastName("Tyson")
                .username("Mike.Tyson")
                .password("hashedPassword")
                .specialization(new TrainingType("BOXING"))
                .isActive(true)
                .build();
    }
}