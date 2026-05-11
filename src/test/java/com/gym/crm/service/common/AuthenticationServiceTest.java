package com.gym.crm.service.common;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.UserDao;
import com.gym.crm.dto.request.UserCredentials;
import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.profile.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService service;

    @Test
    void validateTraineeCredentials_validCredentials_doesNotThrow() {
        UserCredentials credentials = buildCredentials("Abdul.Hariton", "password123");

        when(traineeDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.of(buildTrainee()));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        assertThatCode(() -> service.validateTraineeCredentials(credentials)).doesNotThrowAnyException();

        verify(traineeDao).findByUsername("Abdul.Hariton");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void validateTraineeCredentials_wrongPassword_throwsAuthenticationException() {
        UserCredentials credentials = buildCredentials("Abdul.Hariton", "wrongPassword");

        when(traineeDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.of(buildTrainee()));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> service.validateTraineeCredentials(credentials))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void validateTraineeCredentials_traineeNotFound_throwsAuthenticationException() {
        UserCredentials credentials = buildCredentials("ghost.user", "password123");

        when(traineeDao.findByUsername("ghost.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validateTraineeCredentials(credentials))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");;
    }

    @Test
    void validateTrainerCredentials_validCredentials_doesNotThrow() {
        UserCredentials credentials = buildCredentials("Mike.Tyson", "password123");

        when(trainerDao.findByUsername("Mike.Tyson")).thenReturn(Optional.of(buildTrainer()));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        assertThatCode(() -> service.validateTrainerCredentials(credentials)).doesNotThrowAnyException();

        verify(trainerDao).findByUsername("Mike.Tyson");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void validateTrainerCredentials_wrongPassword_throwsAuthenticationException() {
        UserCredentials credentials = buildCredentials("Mike.Tyson", "wrongPassword");

        when(trainerDao.findByUsername("Mike.Tyson")).thenReturn(Optional.of(buildTrainer()));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> service.validateTrainerCredentials(credentials))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void validateTrainerCredentials_trainerNotFound_throwsAuthenticationException() {
        UserCredentials credentials = buildCredentials("ghost.user", "password123");

        when(trainerDao.findByUsername("ghost.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validateTrainerCredentials(credentials))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void validateCredentials_validCredentials_doesNotThrow() {
        UserCredentials credentials = buildCredentials("Abdul.Hariton", "password123");

        when(userDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.of(buildUser("Abdul.Hariton")));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        assertThatCode(() -> service.validateCredentials(credentials)).doesNotThrowAnyException();

        verify(userDao).findByUsername("Abdul.Hariton");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void validateCredentials_wrongPassword_throwsAuthenticationException() {
        UserCredentials credentials = buildCredentials("Abdul.Hariton", "wrongPassword");

        when(userDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.of(buildUser("Abdul.Hariton")));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> service.validateCredentials(credentials))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void validateCredentials_userNotFound_throwsAuthenticationException() {
        UserCredentials credentials = buildCredentials("ghost.user", "password123");

        when(userDao.findByUsername("ghost.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validateCredentials(credentials))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
    }

    private UserCredentials buildCredentials(String username, String password) {
        return UserCredentials.builder()
                .username(username)
                .password(password)
                .build();
    }

    private User buildUser(String username) {
        return User.builder()
                .id(1L)
                .username(username)
                .password("encodedPassword")
                .isActive(true)
                .build();
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .username("Abdul.Hariton")
                        .password("encodedPassword")
                        .isActive(true)
                        .build())
                .build();
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .id(1L)
                .user(User.builder()
                        .id(2L)
                        .username("Mike.Tyson")
                        .password("encodedPassword")
                        .isActive(true)
                        .build())
                .specialization(TrainingType.builder()
                        .trainingTypeName("BOXING")
                        .build())
                .build();
    }
}