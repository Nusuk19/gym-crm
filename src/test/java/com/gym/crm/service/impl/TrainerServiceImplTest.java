package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dto.request.ActivationRequest;
import com.gym.crm.dto.request.ChangePasswordRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.UserProfileService;
import com.gym.crm.service.UserService;
import com.gym.crm.validator.EntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    private static final Long ID = 1L;
    private static final Long NON_EXISTING_ID = 99L;
    private static final String USERNAME = "Mike.Tyson";

    private final Trainer trainer = buildTrainer();

    @Mock
    private TrainerDao trainerDao;
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private EntityValidator validator;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    private UserService userService;
    @InjectMocks
    private TrainerServiceImpl service;

    @Test
    void create_whenValidTrainer_savesWithGeneratedProfile() {
        when(userProfileService.generateUsername("Mike", "Tyson")).thenReturn("Mike.Tyson");
        when(userProfileService.generatePassword()).thenReturn("rawPass123");
        when(userProfileService.hashPassword("rawPass123")).thenReturn("hashedPass");
        when(trainerDao.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer actual = service.create(trainer);

        assertEquals("Mike.Tyson", actual.getUser().getUsername());
        assertEquals("hashedPass", actual.getUser().getPassword());
        assertNotEquals("rawPass123", actual.getUser().getPassword());
        verify(validator).validateTrainer(trainer);
        verify(userProfileService).generateUsername("Mike", "Tyson");
        verify(userProfileService).generatePassword();
        verify(userProfileService).hashPassword("rawPass123");
        verify(trainerDao).save(any(Trainer.class));
    }

    @Test
    void create_whenValidationFails_throwsExceptionAndDaoNotCalled() {
        doThrow(new EntityValidationException("Trainer cannot be null"))
                .when(validator).validateTrainer(trainer);

        assertThrows(EntityValidationException.class, () -> service.create(trainer));

        verify(trainerDao, never()).save(any());
    }

    @Test
    void update_whenValidTrainer_updatesSuccessfully() {
        Trainer existing = trainer.toBuilder()
                .user(trainer.getUser().toBuilder()
                        .id(ID)
                        .username(USERNAME)
                        .password("existingHash")
                        .build())
                .build();

        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(existing));
        when(trainerDao.update(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer actual = service.update(trainer);

        assertThat(actual.getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getUser().getPassword()).isEqualTo("existingHash");
        verify(validator).validateTrainer(trainer);
        verify(trainerDao).findByUsername(USERNAME);
        verify(trainerDao).update(any(Trainer.class));
    }

    @Test
    void findById_whenTrainerExists_returnsTrainer() {
        when(trainerDao.findById(ID)).thenReturn(Optional.of(trainer));

        Optional<Trainer> actual = service.findById(ID);

        assertTrue(actual.isPresent());
        assertEquals(trainer, actual.get());
        verify(validator).requireValidId(ID);
        verify(trainerDao).findById(ID);
    }

    @Test
    void findById_whenTrainerNotExists_returnsEmpty() {
        when(trainerDao.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<Trainer> actual = service.findById(NON_EXISTING_ID);

        assertFalse(actual.isPresent());
        verify(validator).requireValidId(NON_EXISTING_ID);
        verify(trainerDao).findById(NON_EXISTING_ID);
    }

    @Test
    void findById_whenInvalidId_throwsExceptionAndDaoNotCalled() {
        doThrow(new EntityValidationException("Id must be a positive integer"))
                .when(validator).requireValidId(any());

        assertThrows(EntityValidationException.class, () -> service.findById(0L));

        verify(trainerDao, never()).findById(any());
    }

    @Test
    void findAll_whenTrainersExist_returnsAllTrainers() {
        when(trainerDao.findAll()).thenReturn(List.of(trainer));

        List<Trainer> actual = service.findAll();

        assertEquals(List.of(trainer), actual);
        verify(trainerDao).findAll();
    }

    @Test
    void findAll_whenNoTrainersExist_returnsEmptyList() {
        when(trainerDao.findAll()).thenReturn(List.of());

        List<Trainer> actual = service.findAll();

        assertTrue(actual.isEmpty());
        verify(trainerDao).findAll();
    }

    @Test
    void findAllNotAssignedToTrainee_whenTraineeExists_returnsUnassignedTrainers() {
        Trainee trainee = buildTrainee();
        when(traineeDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.of(trainee));
        when(trainerDao.findAllNotAssignedToTrainee("Abdul.Hariton")).thenReturn(List.of(trainer));

        List<Trainer> actual = service.findAllNotAssignedToTrainee("Abdul.Hariton");

        assertThat(actual.get(0)).isEqualTo(trainer);
        verify(validator).requireNonBlank("Abdul.Hariton", "Username cannot be blank");
        verify(traineeDao).findByUsername("Abdul.Hariton");
        verify(trainerDao).findAllNotAssignedToTrainee("Abdul.Hariton");
    }

    @Test
    void findAllNotAssignedToTrainee_whenTraineeNotFound_throwsEntityNotFoundException() {
        when(traineeDao.findByUsername("ghost.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findAllNotAssignedToTrainee("ghost.user"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Trainee not found")
                .hasMessageContaining("ghost.user");

        verify(trainerDao, never()).findAllNotAssignedToTrainee(anyString());
    }

    @Test
    void findAllNotAssignedToTrainee_whenBlankUsername_throwsAndDaoNotCalled() {
        doThrow(new EntityValidationException("Username cannot be blank"))
                .when(validator).requireNonBlank("", "Username cannot be blank");

        assertThatThrownBy(() -> service.findAllNotAssignedToTrainee(""))
                .isInstanceOf(EntityValidationException.class)
                .hasMessageContaining("Username cannot be blank");

        verify(traineeDao, never()).findByUsername(anyString());
        verify(trainerDao, never()).findAllNotAssignedToTrainee(anyString());
    }

    @Test
    void changePassword_whenTrainerExists_delegatesToUserService() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .username(USERNAME)
                .oldPassword("oldPassword")
                .newPassword("newPassword123")
                .build();

        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));
        doNothing().when(userService).changePassword(request);

        service.changePassword(request);

        verify(trainerDao).findByUsername(USERNAME);
        verify(userService).changePassword(request);
    }

    @Test
    void changePassword_whenTrainerNotFound_throwsEntityNotFoundException() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .username("ghost.user")
                .oldPassword("oldPassword")
                .newPassword("newPassword123")
                .build();

        when(trainerDao.findByUsername("ghost.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changePassword(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Trainer not found")
                .hasMessageContaining("ghost.user");

        verify(userService, never()).changePassword(any());
    }

    @Test
    void activate_whenTrainerExists_delegatesToUserService() {
        ActivationRequest request = ActivationRequest.builder()
                .username(USERNAME)
                .build();

        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));
        doNothing().when(userService).activate(request);

        service.activate(request);

        verify(trainerDao).findByUsername(USERNAME);
        verify(userService).activate(request);
    }

    @Test
    void activate_whenTrainerNotFound_throwsEntityNotFoundException() {
        ActivationRequest request = ActivationRequest.builder()
                .username("ghost.user")
                .build();

        when(trainerDao.findByUsername("ghost.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.activate(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Trainer not found")
                .hasMessageContaining("ghost.user");

        verify(userService, never()).activate(any());
    }

    @Test
    void deactivate_whenTrainerExists_delegatesToUserService() {
        ActivationRequest request = ActivationRequest.builder()
                .username(USERNAME)
                .build();

        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));
        doNothing().when(userService).deactivate(request);

        service.deactivate(request);

        verify(trainerDao).findByUsername(USERNAME);
        verify(userService).deactivate(request);
    }

    @Test
    void deactivate_whenTrainerNotFound_throwsEntityNotFoundException() {
        ActivationRequest request = ActivationRequest.builder()
                .username("ghost.user")
                .build();

        when(trainerDao.findByUsername("ghost.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deactivate(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Trainer not found")
                .hasMessageContaining("ghost.user");

        verify(userService, never()).deactivate(any());
    }

    private Trainer buildTrainer() {
        User user = User.builder()
                .id(ID)
                .firstName("Mike")
                .lastName("Tyson")
                .username(USERNAME)
                .isActive(true)
                .build();

        return Trainer.builder()
                .id(ID)
                .user(user)
                .specialization(TrainingType.builder()
                        .trainingTypeName("BOXING")
                        .build())
                .build();
    }

    private Trainee buildTrainee() {
        User user = User.builder()
                .id(2L)
                .firstName("Abdul")
                .lastName("Hariton")
                .username("Abdul.Hariton")
                .isActive(true)
                .build();

        return Trainee.builder()
                .id(2L)
                .user(user)
                .build();
    }
}