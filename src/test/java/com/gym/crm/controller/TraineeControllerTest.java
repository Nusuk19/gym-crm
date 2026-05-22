package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
import com.gym.crm.facade.GymFacade;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    private static final String USERNAME = "John.Doe";
    private static final String TRAINER_USERNAME = "Mike.Smith";
    private static final String BASE_URL = "/api/v1/trainees";
    private static final String BASE_PATH = "/api/v1";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String PASSWORD = "password123";
    private static final String SPECIALIZATION = "CARDIO";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, 1, 15);
    private static final String ADDRESS = "Kyiv, Ukraine";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private GymFacade facade;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.setMessageInterpolator(new ParameterMessageInterpolator());
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TraineeController(facade))
                .setValidator(validator)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .addPlaceholderValue("app.api.base-path", BASE_PATH)
                .build();
    }

    @Test
    void register_shouldReturnCredentials_whenRequestIsValid() throws Exception {
        TraineeCreateRequest request = buildCreateRequest();
        TraineeCreateResponse response = new TraineeCreateResponse(USERNAME, PASSWORD);

        when(facade.createTrainee(any(TraineeCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.password").value(PASSWORD));

        verify(facade).createTrainee(any(TraineeCreateRequest.class));
    }

    @Test
    void register_shouldReturnCredentials_whenOptionalFieldsAbsent() throws Exception {
        TraineeCreateRequest request = new TraineeCreateRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        TraineeCreateResponse response = new TraineeCreateResponse(USERNAME, PASSWORD);

        when(facade.createTrainee(any(TraineeCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.password").value(PASSWORD));
    }

    @Test
    void register_shouldReturnBadRequest_whenFirstNameIsMissing() throws Exception {
        TraineeCreateRequest request = new TraineeCreateRequest();
        request.setLastName(LAST_NAME);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void register_shouldReturnBadRequest_whenLastNameIsMissing() throws Exception {
        TraineeCreateRequest request = new TraineeCreateRequest();
        request.setFirstName(FIRST_NAME);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void getTraineeProfile_shouldReturnTrainee_whenExists() throws Exception {
        TraineeGetResponse response = buildGetResponse();

        when(facade.getTraineeByUsername(USERNAME)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainers").isArray())
                .andExpect(jsonPath("$.trainers.length()").value(1))
                .andExpect(jsonPath("$.trainers[0].username").value(TRAINER_USERNAME));

        verify(facade).getTraineeByUsername(USERNAME);
    }

    @Test
    void getTraineeProfile_shouldReturnBadRequest_whenUsernameFormatIsInvalid() throws Exception {
        mockMvc.perform(get(BASE_URL + "/invalid-username-format"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void updateTraineeProfile_shouldReturnUpdatedTrainee_whenRequestIsValid() throws Exception {
        TraineeUpdateRequest request = buildUpdateRequest();
        TraineeUpdateResponse response = buildUpdateResponse();

        when(facade.updateTrainee(eq(USERNAME), any(TraineeUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainers").isArray());

        verify(facade).updateTrainee(eq(USERNAME), any(TraineeUpdateRequest.class));
    }

    @Test
    void updateTraineeProfile_shouldReturnBadRequest_whenFirstNameIsMissing() throws Exception {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        request.setLastName(LAST_NAME);
        request.isActive(true);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void deleteTrainee_shouldReturnOk_whenUsernameIsValid() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk());

        verify(facade).deleteTraineeByUsername(USERNAME);
    }

    @Test
    void deleteTrainee_shouldReturnBadRequest_whenUsernameFormatIsInvalid() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/bad_username"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void getAvailableTrainers_shouldReturnList_whenTrainersExist() throws Exception {
        List<AssignedTrainerResponse> response = List.of(buildAssignedTrainerResponse());

        when(facade.findAllTrainersNotAssignedToTrainee(USERNAME)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/available-trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value(TRAINER_USERNAME))
                .andExpect(jsonPath("$[0].firstName").value("Mike"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[0].specialization").value(SPECIALIZATION));

        verify(facade).findAllTrainersNotAssignedToTrainee(USERNAME);
    }

    @Test
    void getAvailableTrainers_shouldReturnEmptyList_whenNoTrainersAvailable() throws Exception {
        when(facade.findAllTrainersNotAssignedToTrainee(USERNAME)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/available-trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void updateTraineeTrainers_shouldReturnUpdatedList_whenRequestIsValid() throws Exception {
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest(List.of(TRAINER_USERNAME));
        TraineeAssignedTrainersUpdateResponse response = buildAssignedTrainersUpdateResponse();

        when(facade.updateTraineeTrainers(eq(USERNAME), any(TraineeAssignedTrainersUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainers").isArray())
                .andExpect(jsonPath("$.trainers.length()").value(1))
                .andExpect(jsonPath("$.trainers[0].username").value(TRAINER_USERNAME))
                .andExpect(jsonPath("$.trainers[0].specialization").value(SPECIALIZATION));

        verify(facade).updateTraineeTrainers(eq(USERNAME), any(TraineeAssignedTrainersUpdateRequest.class));
    }

    @Test
    void getTraineeTrainings_shouldReturnList_whenNoFilters() throws Exception {
        List<GetTraineeTrainingResponse> response = List.of(buildTrainingResponse());

        when(facade.findTrainingsByTraineeCriteria(USERNAME, null, null, null, null))
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].trainingName").value("Morning Run"))
                .andExpect(jsonPath("$[0].trainingType").value(SPECIALIZATION))
                .andExpect(jsonPath("$[0].trainerName").value(TRAINER_USERNAME));

        verify(facade).findTrainingsByTraineeCriteria(USERNAME, null, null, null, null);
    }

    @Test
    void getTraineeTrainings_shouldPassFilters_whenProvided() throws Exception {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);

        when(facade.findTrainingsByTraineeCriteria(
                USERNAME, from, to, TRAINER_USERNAME, SPECIALIZATION))
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/trainings")
                        .param("fromDate", "2024-01-01")
                        .param("toDate", "2024-12-31")
                        .param("trainerName", TRAINER_USERNAME)
                        .param("trainingType", SPECIALIZATION))
                .andExpect(status().isOk());

        verify(facade).findTrainingsByTraineeCriteria(
                USERNAME, from, to, TRAINER_USERNAME, SPECIALIZATION);
    }

    @Test
    void changeActivationStatus_shouldReturnOk_whenActivating() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest(true);

        mockMvc.perform(patch(BASE_URL + "/" + USERNAME + "/activation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(facade).changeTraineeActivationStatus(eq(USERNAME), any(ActivationStatusRequest.class));
    }

    @Test
    void changeActivationStatus_shouldReturnOk_whenDeactivating() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest(false);

        mockMvc.perform(patch(BASE_URL + "/" + USERNAME + "/activation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(facade).changeTraineeActivationStatus(eq(USERNAME), any(ActivationStatusRequest.class));
    }

    @Test
    void changeActivationStatus_shouldReturnBadRequest_whenIsActiveIsMissing() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/" + USERNAME + "/activation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    private TraineeCreateRequest buildCreateRequest() {
        TraineeCreateRequest request = new TraineeCreateRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setDateOfBirth(DATE_OF_BIRTH);
        request.setAddress(ADDRESS);

        return request;
    }

    private TraineeUpdateRequest buildUpdateRequest() {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setDateOfBirth(DATE_OF_BIRTH);
        request.setAddress(ADDRESS);
        request.isActive(true);

        return request;
    }

    private TraineeGetResponse buildGetResponse() {
        TraineeGetResponse response = new TraineeGetResponse();
        response.setFirstName(FIRST_NAME);
        response.setLastName(LAST_NAME);
        response.setDateOfBirth(DATE_OF_BIRTH);
        response.setAddress(ADDRESS);
        response.isActive(true);
        response.setTrainers(List.of(buildAssignedTrainerResponse()));

        return response;
    }

    private TraineeUpdateResponse buildUpdateResponse() {
        TraineeUpdateResponse response = new TraineeUpdateResponse();
        response.setUsername(USERNAME);
        response.setFirstName(FIRST_NAME);
        response.setLastName(LAST_NAME);
        response.setDateOfBirth(DATE_OF_BIRTH);
        response.setAddress(ADDRESS);
        response.isActive(true);
        response.setTrainers(List.of(buildAssignedTrainerResponse()));

        return response;
    }

    private AssignedTrainerResponse buildAssignedTrainerResponse() {
        AssignedTrainerResponse response = new AssignedTrainerResponse();
        response.setUsername(TRAINER_USERNAME);
        response.setFirstName("Mike");
        response.setLastName("Smith");
        response.setSpecialization(SPECIALIZATION);

        return response;
    }

    private TraineeAssignedTrainersUpdateResponse buildAssignedTrainersUpdateResponse() {
        TraineeAssignedTrainersUpdateResponse response = new TraineeAssignedTrainersUpdateResponse();
        response.setTrainers(List.of(buildAssignedTrainerResponse()));

        return response;
    }

    private GetTraineeTrainingResponse buildTrainingResponse() {
        GetTraineeTrainingResponse response = new GetTraineeTrainingResponse();
        response.setTrainingName("Morning Run");
        response.setTrainingDate(LocalDate.of(2024, 5, 1));
        response.setTrainingType(SPECIALIZATION);
        response.setTrainingDuration(60);
        response.setTrainerName(TRAINER_USERNAME);

        return response;
    }
}