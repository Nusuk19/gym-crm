package com.gym.crm.service.impl;

import com.gym.crm.dao.UserDao;
import com.gym.crm.dto.request.ActivationRequest;
import com.gym.crm.dto.request.ChangePasswordRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.User;
import com.gym.crm.profile.PasswordEncoder;
import com.gym.crm.service.common.ValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ValidationService validationService;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findByUsername_existingUser_returnsUser() {
        when(userDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.of(buildActiveUser()));

        Optional<User> result = userService.findByUsername("Abdul.Hariton");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("Abdul.Hariton");
        assertThat(result.get().getFirstName()).isEqualTo("Abdul");
        assertThat(result.get().getLastName()).isEqualTo("Hariton");
        assertThat(result.get().getIsActive()).isTrue();
        verify(userDao).findByUsername("Abdul.Hariton");
    }

    @Test
    void findByUsername_nonExistingUser_returnsEmpty() {
        when(userDao.findByUsername("ghost.user")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("ghost.user");

        assertThat(result).isEmpty();
        verify(userDao).findByUsername("ghost.user");
    }

    @Test
    void changePassword_validRequest_updatesPassword() {
        ChangePasswordRequest request = buildChangePasswordRequest("Abdul.Hariton", "rawOldPassword", "newPassword123");

        when(userDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.of(buildActiveUser()));
        when(passwordEncoder.matches("rawOldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userDao.update(any(User.class))).thenReturn(buildActiveUser());

        userService.changePassword(request);

        verify(userDao).findByUsername("Abdul.Hariton");
        verify(passwordEncoder).matches("rawOldPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword123");
        verify(userDao).update(any(User.class));
    }

    @Test
    void changePassword_wrongOldPassword_throwsEntityValidationException() {
        ChangePasswordRequest request = buildChangePasswordRequest("Abdul.Hariton", "wrongOldPassword", "newPassword123");

        when(userDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.of(buildActiveUser()));
        when(passwordEncoder.matches("wrongOldPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(EntityValidationException.class)
                .hasMessageContaining("Old password does not match")
                .hasMessageContaining("Abdul.Hariton");

        verify(passwordEncoder, never()).encode(anyString());
        verify(userDao, never()).update(any());
    }

    @Test
    void changePassword_userNotFound_throwsEntityNotFoundException() {
        ChangePasswordRequest request = buildChangePasswordRequest("ghost.user", "oldPassword", "newPassword123");

        when(userDao.findByUsername("ghost.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining("ghost.user");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userDao, never()).update(any());
    }

    @Test
    void activate_inactiveUser_activatesSuccessfully() {
        ActivationRequest request = buildActivationRequest("Mike.Tyson");

        when(userDao.findByUsername("Mike.Tyson")).thenReturn(Optional.of(buildInactiveUser()));
        when(userDao.update(any(User.class))).thenReturn(buildInactiveUser());

        userService.activate(request);

        verify(userDao).findByUsername("Mike.Tyson");
        verify(userDao).update(any(User.class));
    }

    @Test
    void activate_alreadyActiveUser_throwsEntityValidationException() {
        ActivationRequest request = buildActivationRequest("Abdul.Hariton");

        when(userDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.of(buildActiveUser()));

        assertThatThrownBy(() -> userService.activate(request))
                .isInstanceOf(EntityValidationException.class)
                .hasMessageContaining("User is already active")
                .hasMessageContaining("Abdul.Hariton");

        verify(userDao, never()).update(any());
    }

    @Test
    void activate_userNotFound_throwsEntityNotFoundException() {
        ActivationRequest request = buildActivationRequest("ghost.user");

        when(userDao.findByUsername("ghost.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.activate(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining("ghost.user");

        verify(userDao, never()).update(any());
    }

    @Test
    void deactivate_activeUser_deactivatesSuccessfully() {
        ActivationRequest request = buildActivationRequest("Abdul.Hariton");

        when(userDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.of(buildActiveUser()));
        when(userDao.update(any(User.class))).thenReturn(buildActiveUser());

        userService.deactivate(request);

        verify(userDao).findByUsername("Abdul.Hariton");
        verify(userDao).update(any(User.class));
    }

    @Test
    void deactivate_alreadyInactiveUser_throwsEntityValidationException() {
        ActivationRequest request = buildActivationRequest("Mike.Tyson");

        when(userDao.findByUsername("Mike.Tyson")).thenReturn(Optional.of(buildInactiveUser()));

        assertThatThrownBy(() -> userService.deactivate(request))
                .isInstanceOf(EntityValidationException.class)
                .hasMessageContaining("User is already inactive")
                .hasMessageContaining("Mike.Tyson");

        verify(userDao, never()).update(any());
    }

    @Test
    void deactivate_userNotFound_throwsEntityNotFoundException() {
        ActivationRequest request = buildActivationRequest("ghost.user");

        when(userDao.findByUsername("ghost.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deactivate(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining("ghost.user");

        verify(userDao, never()).update(any());
    }

    private User buildActiveUser() {
        return User.builder()
                .id(1L)
                .firstName("Abdul")
                .lastName("Hariton")
                .username("Abdul.Hariton")
                .password("encodedPassword")
                .isActive(true)
                .build();
    }

    private User buildInactiveUser() {
        return User.builder()
                .id(2L)
                .firstName("Mike")
                .lastName("Tyson")
                .username("Mike.Tyson")
                .password("encodedPassword2")
                .isActive(false)
                .build();
    }

    private ChangePasswordRequest buildChangePasswordRequest(String username, String oldPassword, String newPassword) {
        return ChangePasswordRequest.builder()
                .username(username)
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();
    }

    private ActivationRequest buildActivationRequest(String username) {
        return ActivationRequest.builder()
                .username(username)
                .build();
    }
}