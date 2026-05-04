package com.gym.crm.mapper;

import com.gym.crm.dto.request.CreateTrainerRequest;
import com.gym.crm.dto.request.UpdateTrainerRequest;
import com.gym.crm.dto.response.TrainerResponse;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

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
        trainerMapper = Mappers.getMapper(TrainerMapper.class);
    }

    @Test
    void toEntity_fromCreateRequest_mapsAllFieldsCorrectly() {
        CreateTrainerRequest request = buildCreateRequest();

        Trainer actual = trainerMapper.toEntity(request);

        assertEquals("Mike", actual.getUser().getFirstName());
        assertEquals("Tyson", actual.getUser().getLastName());
        assertEquals("BOXING", actual.getSpecialization().getTrainingTypeName());
        assertTrue(actual.getUser().getIsActive());
    }

    @Test
    void toEntity_fromCreateRequest_doesNotSetIdUsernameOrPassword() {
        CreateTrainerRequest request = CreateTrainerRequest.builder()
                .firstName("Mike")
                .lastName("Tyson")
                .specialization(TrainingType.builder().trainingTypeName("BOXING").build())
                .build();

        Trainer actual = trainerMapper.toEntity(request);

        assertNull(actual.getId());
        assertNull(actual.getUser().getUsername());
        assertNull(actual.getUser().getPassword());
    }

    @Test
    void toEntity_fromUpdateRequest_mapsAllFieldsCorrectly() {
        UpdateTrainerRequest request = buildUpdateRequest();

        Trainer actual = trainerMapper.toEntity(request);

        assertEquals("Mike", actual.getUser().getFirstName());
        assertEquals("Tyson", actual.getUser().getLastName());
        assertEquals("YOGA", actual.getSpecialization().getTrainingTypeName());
        assertFalse(actual.getUser().getIsActive());
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
        assertTrue(actual.getIsActive());
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
                .specialization(TrainingType.builder().trainingTypeName("BOXING").build())
                .isActive(true)
                .build();
    }

    private UpdateTrainerRequest buildUpdateRequest() {
        return UpdateTrainerRequest.builder()
                .username("Mike.Tyson")
                .firstName("Mike")
                .lastName("Tyson")
                .specialization(TrainingType.builder().trainingTypeName("YOGA").build())
                .isActive(false)
                .build();
    }

    private Trainer buildTrainer() {
        User user = User.builder()
                .id(EXISTING_ID)
                .firstName("Mike")
                .lastName("Tyson")
                .username("Mike.Tyson")
                .password("hashedPassword")
                .isActive(true)
                .build();

        return Trainer.builder()
                .id(EXISTING_ID)
                .user(user)
                .specialization(TrainingType.builder().trainingTypeName("BOXING").build())
                .build();
    }
}