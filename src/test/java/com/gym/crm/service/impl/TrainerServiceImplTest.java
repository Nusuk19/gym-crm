package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.UserProfileService;
import com.gym.crm.validator.EntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    private EntityValidator validator;
    @Mock
    private UserProfileService userProfileService;
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
}