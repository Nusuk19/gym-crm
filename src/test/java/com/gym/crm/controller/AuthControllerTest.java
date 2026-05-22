package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.facade.GymFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String BASE_PATH = "/api/v1";
    private static final String USERNAME = "john.doe";
    private static final String PASSWORD = "password123";
    private static final String NEW_PASSWORD = "newPassword123";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private GymFacade facade;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(facade))
                .setValidator(validator)
                .addPlaceholderValue("app.api.base-path", BASE_PATH)
                .build();
    }

    @Test
    void login_validRequest_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(USERNAME, PASSWORD))))
                .andExpect(status().isOk());

        verify(facade).login(any(LoginRequest.class));
    }

    @Test
    void login_validRequest_storesUsernameInSession() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(USERNAME, PASSWORD)))
                        .session(session))
                .andExpect(status().isOk());

        assertThat(session.getAttribute("username")).isEqualTo(USERNAME);
    }

    @Test
    void login_nullUsername_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(null, PASSWORD))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void login_nullPassword_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(USERNAME, null))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void changePassword_validRequest_returns200() throws Exception {
        mockMvc.perform(put("/api/v1/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginChangeRequest(USERNAME, PASSWORD, NEW_PASSWORD))))
                .andExpect(status().isOk());

        verify(facade).changePassword(any(LoginChangeRequest.class));
    }

    @Test
    void changePassword_nullUsername_returns400() throws Exception {
        mockMvc.perform(put("/api/v1/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginChangeRequest(null, PASSWORD, NEW_PASSWORD))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }
}