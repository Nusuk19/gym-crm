package com.gym.crm.mapper;

import com.gym.crm.dto.request.CreateTraineeRequest;
import com.gym.crm.dto.request.UpdateTraineeRequest;
import com.gym.crm.dto.response.TraineeResponse;
import com.gym.crm.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraineeMapperTest {

    private static final Long EXISTING_ID = 1L;

    private TraineeMapper traineeMapper;


    @BeforeEach
    void setUp() {
        traineeMapper = new TraineeMapper();
    }

    @Test
    void toEntity_fromCreateRequest_mapsAllFieldsCorrectly() {
        CreateTraineeRequest request = buildCreateRequest();

        Trainee actual = traineeMapper.toEntity(request);

        assertEquals("Abdul", actual.getFirstName());
        assertEquals("Hariton", actual.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), actual.getDateOfBirth());
        assertEquals("Kyiv", actual.getAddress());
        assertTrue(actual.isActive());
    }

    @Test
    void toEntity_fromCreateRequest_doesNotSetIdUsernameOrPassword() {
        CreateTraineeRequest request = CreateTraineeRequest.builder()
                .firstName("Abdul")
                .lastName("HaritonHariton")
                .build();

        Trainee actual = traineeMapper.toEntity(request);

        assertNull(actual.getUserId());
        assertNull(actual.getUsername());
        assertNull(actual.getPassword());
    }

    @Test
    void toEntity_fromUpdateRequest_mapsAllFieldsCorrectly() {
        UpdateTraineeRequest request = buildUpdateRequest();

        Trainee actual = traineeMapper.toEntity(request);

        assertEquals(EXISTING_ID, actual.getUserId());
        assertEquals("Abdul", actual.getFirstName());
        assertEquals("Hariton", actual.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), actual.getDateOfBirth());
        assertEquals("Lviv", actual.getAddress());
        assertFalse(actual.isActive());
        assertEquals("Abdul.Hariton", actual.getUsername());
    }

    @Test
    void toResponse_fromTrainee_mapsAllFieldsCorrectly() {
        Trainee trainee = buildTrainee();

        TraineeResponse actual = traineeMapper.toResponse(trainee);

        assertEquals(EXISTING_ID, actual.getId());
        assertEquals("Abdul", actual.getFirstName());
        assertEquals("Hariton", actual.getLastName());
        assertEquals("Abdul.Hariton", actual.getUsername());
        assertEquals(LocalDate.of(1990, 1, 1), actual.getDateOfBirth());
        assertEquals("Kyiv", actual.getAddress());
        assertTrue(actual.isActive());
    }

    @Test
    void toResponse_passwordFieldNotPresentInResponse() {
        assertThrows(NoSuchFieldException.class,
                () -> TraineeResponse.class.getDeclaredField("password"));
    }


    private CreateTraineeRequest buildCreateRequest() {
        return CreateTraineeRequest.builder()
                .firstName("Abdul")
                .lastName("Hariton")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Kyiv")
                .isActive(true)
                .build();
    }

    private UpdateTraineeRequest buildUpdateRequest() {
        return UpdateTraineeRequest.builder()
                .id(EXISTING_ID)
                .firstName("Abdul")
                .lastName("Hariton")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Lviv")
                .isActive(false)
                .username("Abdul.Hariton")
                .build();
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .userId(EXISTING_ID)
                .firstName("Abdul")
                .lastName("Hariton")
                .username("Abdul.Hariton")
                .password("hashedPassword")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Kyiv")
                .isActive(true)
                .build();
    }

}