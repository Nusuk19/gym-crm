package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gia.openapi.model.TrainingCreateRequest;
import com.gia.openapi.model.TrainingTypeResponse;
import com.gym.crm.facade.GymFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    private static final String BASE_URL = "/api/v1/trainings";
    private static final String BASE_PATH = "/api/v1";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private GymFacade facade;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainingController(facade))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .addPlaceholderValue("app.api.base-path", BASE_PATH)
                .build();
    }

    @Test
    void addTraining_shouldReturnOk_whenRequestIsValid() throws Exception {
        TrainingCreateRequest request = buildCreateRequest();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(facade).createTraining(any(TrainingCreateRequest.class));
    }

    @Test
    void addTraining_shouldReturnBadRequest_whenTraineeUsernameIsMissing() throws Exception {
        TrainingCreateRequest request = buildCreateRequest();
        request.setTraineeUsername(null);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void addTraining_shouldReturnBadRequest_whenTrainerUsernameIsMissing() throws Exception {
        TrainingCreateRequest request = buildCreateRequest();
        request.setTrainerUsername(null);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void addTraining_shouldReturnBadRequest_whenTrainingNameIsMissing() throws Exception {
        TrainingCreateRequest request = buildCreateRequest();
        request.setTrainingName(null);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void addTraining_shouldReturnBadRequest_whenTrainingDateIsMissing() throws Exception {
        TrainingCreateRequest request = buildCreateRequest();
        request.setTrainingDate(null);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void addTraining_shouldReturnBadRequest_whenTrainingDurationIsMissing() throws Exception {
        TrainingCreateRequest request = buildCreateRequest();
        request.setTrainingDuration(null);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void getTrainingTypes_shouldReturnList_whenTypesExist() throws Exception {
        List<TrainingTypeResponse> response = List.of(
                buildTrainingTypeResponse(1, "BOXING"),
                buildTrainingTypeResponse(2, "CARDIO"));

        when(facade.findAllTrainingTypes()).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("BOXING"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("CARDIO"));

        verify(facade).findAllTrainingTypes();
    }

    @Test
    void getTrainingTypes_shouldReturnEmptyList_whenNoTypesExist() throws Exception {
        when(facade.findAllTrainingTypes()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(facade).findAllTrainingTypes();
    }

    private TrainingCreateRequest buildCreateRequest() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeUsername("Abdul.Hariton");
        request.setTrainerUsername("Mike.Tyson");
        request.setTrainingName("Boxing basics");
        request.setTrainingDate(LocalDate.of(2024, 5, 1));
        request.setTrainingDuration(60);

        return request;
    }

    private TrainingTypeResponse buildTrainingTypeResponse(int id, String name) {
        TrainingTypeResponse response = new TrainingTypeResponse();
        response.setId(id);
        response.setName(name);

        return response;
    }
}