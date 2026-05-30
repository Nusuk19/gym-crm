package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.config.SecurityConfig;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.util.JsonResourceReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    private static final String USERNAME = "Abdul.Hariton";
    private static final String PASSWORD = "password123";
    private static final String NEW_PASSWORD = "newPassword123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GymFacade facade;

    @Test
    void login_validRequest_returns200() throws Exception {
        String request = JsonResourceReader.readResource("/json/auth/auth-login-request.json");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
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
        String request = JsonResourceReader.readResource("/json/auth/auth-change-password-request.json");

        mockMvc.perform(put("/api/v1/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
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