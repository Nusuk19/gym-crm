package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.service.UserProfileService;
import com.gym.crm.validator.EntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

        assertEquals("Mike.Tyson", actual.getUsername());
        assertEquals("hashedPass", actual.getPassword());
        assertNotEquals("rawPass123", actual.getPassword());
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
        when(trainerDao.update(trainer)).thenReturn(trainer);

        Trainer actual = service.update(trainer);

        assertEquals(trainer, actual);
        verify(validator).validateForUpdate(trainer, trainer.getUserId());
        verify(trainerDao).update(trainer);
    }

    @Test
    void update_whenInvalidId_throwsExceptionAndDaoNotCalled() {
        doThrow(new EntityValidationException("Id must be a positive integer"))
                .when(validator).validateForUpdate(any(), any());

        assertThrows(EntityValidationException.class, () -> service.update(trainer));

        verify(trainerDao, never()).update(any());
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
        return Trainer.builder()
                .userId(ID)
                .firstName("Mike")
                .lastName("Tyson")
                .specialization(new TrainingType("BOXING"))
                .isActive(true)
                .build();
    }
}