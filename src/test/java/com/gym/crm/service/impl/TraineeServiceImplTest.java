package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.Trainee;
import com.gym.crm.service.UserProfileService;
import com.gym.crm.validator.EntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
class TraineeServiceImplTest {

    private static final Long ID = 1L;
    private static final Long NON_EXISTING_ID = 99L;

    @Mock
    private TraineeDao traineeDao;
    @Mock
    private EntityValidator validator;
    @Mock
    private UserProfileService userProfileService;
    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = buildTrainee();
    }

    @Test
    void create_whenValidTrainee_savesWithGeneratedProfile() {
        when(userProfileService.generateUsername("Abdul", "Hariton")).thenReturn("Abdul.Hariton");
        when(userProfileService.generatePassword()).thenReturn("rawPass123");
        when(userProfileService.hashPassword("rawPass123")).thenReturn("hashedPass");
        when(traineeDao.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee actual = traineeService.create(trainee);

        assertEquals("Abdul.Hariton", actual.getUsername());
        assertEquals("hashedPass", actual.getPassword());
        assertNotEquals("rawPass123", actual.getPassword());
        verify(validator).validateTrainee(trainee);
        verify(userProfileService).generateUsername("Abdul", "Hariton");
        verify(userProfileService).generatePassword();
        verify(userProfileService).hashPassword("rawPass123");
        verify(traineeDao).save(any(Trainee.class));
    }

    @Test
    void create_whenValidationFails_throwsException() {
        doThrow(new EntityValidationException("Trainee cannot be null"))
                .when(validator).validateTrainee(any());

        assertThrows(EntityValidationException.class, () -> traineeService.create(null));

        verify(traineeDao, never()).save(any());
    }

    @Test
    void update_whenValidTrainee_updatesSuccessfully() {
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee actual = traineeService.update(trainee);

        assertEquals(trainee, actual);
        verify(validator).validateForUpdate(trainee, trainee.getUserId());
        verify(traineeDao).update(trainee);
    }

    @Test
    void update_whenInvalidId_throwsException() {
        Trainee invalidTrainee = Trainee.builder()
                .userId(null)
                .firstName("Abdul")
                .lastName("Hariton")
                .build();
        doThrow(new EntityValidationException("Id must be a positive integer"))
                .when(validator).validateForUpdate(any(), any());

        assertThrows(EntityValidationException.class, () -> traineeService.update(invalidTrainee));

        verify(traineeDao, never()).update(any());
    }

    @Test
    void delete_whenValidId_deletesSuccessfully() {
        traineeService.delete(ID);

        verify(validator).requireValidId(ID);
        verify(traineeDao).delete(ID);
    }

    @Test
    void delete_whenInvalidId_throwsException() {
        doThrow(new EntityValidationException("Id must be a positive integer"))
                .when(validator).requireValidId(any());

        assertThrows(EntityValidationException.class, () -> traineeService.delete(-ID));

        verify(traineeDao, never()).delete(any());
    }

    @Test
    void findById_whenTraineeExists_returnsTrainee() {
        when(traineeDao.findById(ID)).thenReturn(Optional.of(trainee));

        Optional<Trainee> actual = traineeService.findById(ID);

        assertTrue(actual.isPresent());
        assertEquals(trainee, actual.get());
        verify(validator).requireValidId(ID);
    }

    @Test
    void findById_whenTraineeNotExists_returnsEmpty() {
        when(traineeDao.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<Trainee> actual = traineeService.findById(NON_EXISTING_ID);

        assertFalse(actual.isPresent());
        verify(validator).requireValidId(NON_EXISTING_ID);
    }

    @Test
    void findById_whenInvalidId_throwsException() {
        doThrow(new EntityValidationException("Id must be a positive integer"))
                .when(validator).requireValidId(any());

        assertThrows(EntityValidationException.class, () -> traineeService.findById(0L));

        verify(traineeDao, never()).findById(any());
    }

    @Test
    void findAll_returnsAllTrainees() {
        List<Trainee> trainees = List.of(trainee);
        when(traineeDao.findAll()).thenReturn(trainees);

        List<Trainee> actual = traineeService.findAll();

        assertEquals(1, actual.size());
        assertTrue(actual.contains(trainee));
    }

    @Test
    void findAll_whenEmpty_returnsEmptyList() {
        when(traineeDao.findAll()).thenReturn(List.of());

        List<Trainee> actual = traineeService.findAll();

        assertTrue(actual.isEmpty());
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .userId(ID)
                .firstName("Abdul")
                .lastName("Hariton")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Kyiv")
                .isActive(true)
                .build();
    }
}