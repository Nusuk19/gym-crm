package com.gym.crm.exception;

import org.hibernate.HibernateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("MethodArgumentNotValidException → 400, code 2760, field details included")
    void handleMethodArgumentNotValid_returns400WithDetails() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("obj", "firstName", "must not be blank"),
                new FieldError("obj", "lastName", "must not be blank")));

        ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("errorCode", 2760);
        assertThat(response.getBody().get("errorMessage").toString()).contains("firstName").contains("lastName");
    }

    @Test
    @DisplayName("EntityValidationException → 400, code 2760, message included")
    void handleEntityValidation_returns400WithMessage() {
        EntityValidationException ex = new EntityValidationException("trainingDuration must be positive");

        ResponseEntity<Map<String, Object>> response = handler.handleEntityValidation(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("errorCode", 2760);
        assertThat(response.getBody().get("errorMessage").toString()).contains("trainingDuration must be positive");
    }

    @Test
    @DisplayName("AuthenticationFailedException → 401, code 2805, no internal details leaked")
    void handleAuthentication_returns401WithoutDetails() {
        AuthenticationFailedException ex = new AuthenticationFailedException("Invalid credentials");

        ResponseEntity<Map<String, Object>> response = handler.handleAuthentication(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody()).containsEntry("errorCode", 2805);
        assertThat(response.getBody()).containsEntry("errorMessage", "Authentication fails");
    }

    @Test
    @DisplayName("AuthorizationException → 401, code 2806, no internal details leaked")
    void handleAuthorization_returns401WithoutDetails() {
        AuthorizationException ex = new AuthorizationException("User is not authorized");

        ResponseEntity<Map<String, Object>> response = handler.handleAuthorization(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody()).containsEntry("errorCode", 2806);
        assertThat(response.getBody()).containsEntry("errorMessage", "User is not authorized for request operation");
    }

    @Test
    @DisplayName("EntityNotFoundException → 404, code 2835, entity details included")
    void handleNotFound_returns404WithDetails() {
        EntityNotFoundException ex = new EntityNotFoundException("Trainee not found: john.doe");

        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).containsEntry("errorCode", 2835);
        assertThat(response.getBody().get("errorMessage").toString()).contains("john.doe");
    }

    @Test
    @DisplayName("HibernateException → 500, code 3358, no internal details leaked")
    void handleHibernate_returns500WithoutDetails() {
        HibernateException ex = new HibernateException("connection refused to 192.168.1.1");

        ResponseEntity<Map<String, Object>> response = handler.handleHibernate(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).containsEntry("errorCode", 3358);
        assertThat(response.getBody()).containsEntry("errorMessage", "Unexpected database access failure");
    }

    @Test
    @DisplayName("Generic Exception → 500, code 3200, no internal details leaked")
    void handleGeneric_returns500WithoutDetails() {
        RuntimeException ex = new RuntimeException("NullPointerException at line 42");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).containsEntry("errorCode", 3200);
        assertThat(response.getBody()).containsEntry("errorMessage", "Internal processing error");
    }
}