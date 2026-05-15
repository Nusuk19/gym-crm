package com.gym.crm.controller;

import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.dto.request.ChangePasswordRequest;
import com.gym.crm.dto.request.UserCredentials;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.mapper.AuthMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Mock
    private GymFacade gymFacade;

    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_validRequest_returns200AndCallsFacade() {
        LoginRequest request = buildLoginRequest();
        UserCredentials credentials = buildUserCredentials();

        when(authMapper.toCredentials(request)).thenReturn(credentials);

        ResponseEntity<Void> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(gymFacade).login(credentials);
    }

    @Test
    void changePassword_validRequest_returns200AndCallsFacade() {
        LoginChangeRequest request = buildLoginChangeRequest();
        ChangePasswordRequest changeRequest = buildChangePasswordRequest();

        when(authMapper.toChangePassword(request)).thenReturn(changeRequest);

        ResponseEntity<Void> response = authController.changePassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(gymFacade).changePassword(changeRequest);
    }

    private LoginRequest buildLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john.doe");
        request.setPassword("securePass1");

        return request;
    }

    private LoginChangeRequest buildLoginChangeRequest() {
        LoginChangeRequest request = new LoginChangeRequest();
        request.setUsername("john.doe");
        request.setOldPassword("oldPassword1");
        request.setNewPassword("newPassword123");

        return request;
    }

    private UserCredentials buildUserCredentials() {
        return UserCredentials.builder()
                .username("john.doe")
                .password("securePass1")
                .build();
    }

    private ChangePasswordRequest buildChangePasswordRequest() {
        return ChangePasswordRequest.builder()
                .username("john.doe")
                .oldPassword("oldPassword1")
                .newPassword("newPassword123")
                .build();
    }
}