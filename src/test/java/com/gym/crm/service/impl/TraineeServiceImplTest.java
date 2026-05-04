package com.gym.crm.service.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.dao.TraineeDao;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.User;
import com.gym.crm.service.UserProfileService;
import com.gym.crm.validator.EntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    private final Trainee trainee = buildTrainee();

    @Mock
    private TraineeDao traineeDao;
    @Mock
    private EntityValidator validator;
    @Mock
    private UserProfileService userProfileService;
    @InjectMocks
    private TraineeServiceImpl service;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(TraineeServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void create_whenValidTrainee_savesWithGeneratedProfile() {
        when(userProfileService.generateUsername("Abdul", "Hariton")).thenReturn("Abdul.Hariton");
        when(userProfileService.generatePassword()).thenReturn("rawPass123");
        when(userProfileService.hashPassword("rawPass123")).thenReturn("hashedPass");
        when(traineeDao.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee actual = service.create(trainee);

        assertEquals("Abdul.Hariton", actual.getUser().getUsername());
        assertEquals("hashedPass", actual.getUser().getPassword());
        assertNotEquals("rawPass123", actual.getUser().getPassword());
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

        assertThrows(EntityValidationException.class, () -> service.create(null));

        verify(traineeDao, never()).save(any());
    }

    @Test
    void update_whenValidTrainee_updatesSuccessfully() {
        Trainee existing = trainee.toBuilder()
                .user(trainee.getUser().toBuilder()
                        .id(ID)
                        .username("Abdul.Hariton")
                        .password("existingHash")
                        .build())
                .build();

        when(traineeDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.of(existing));
        when(traineeDao.update(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee actual = service.update(trainee);

        assertEquals(trainee, actual);
        verify(validator).validateTrainee(trainee);
        verify(traineeDao).findByUsername("Abdul.Hariton");
        verify(traineeDao).update(any(Trainee.class));
    }

    @Test
    void update_whenTraineeNotFound_throwsEntityNotFoundException() {
        when(traineeDao.findByUsername("Abdul.Hariton")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(trainee));

        verify(traineeDao, never()).update(any());
    }

    @Test
    void delete_whenValidId_deletesSuccessfully() {
        service.delete(ID);

        verify(validator).requireValidId(ID);
        verify(traineeDao).deleteById(ID);
    }

    @Test
    void delete_whenInvalidId_throwsException() {
        doThrow(new EntityValidationException("Id must be a positive integer"))
                .when(validator).requireValidId(any());

        assertThrows(EntityValidationException.class, () -> service.delete(-ID));

        verify(traineeDao, never()).deleteById(any());
    }

    @Test
    void findById_whenTraineeExists_returnsTrainee() {
        when(traineeDao.findById(ID)).thenReturn(Optional.of(trainee));

        Optional<Trainee> actual = service.findById(ID);

        assertTrue(actual.isPresent());
        assertEquals(trainee, actual.get());
        verify(validator).requireValidId(ID);
    }

    @Test
    void findById_whenTraineeNotExists_returnsEmpty() {
        when(traineeDao.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<Trainee> actual = service.findById(NON_EXISTING_ID);

        assertFalse(actual.isPresent());
        verify(validator).requireValidId(NON_EXISTING_ID);
    }

    @Test
    void findById_whenInvalidId_throwsException() {
        doThrow(new EntityValidationException("Id must be a positive integer"))
                .when(validator).requireValidId(any());

        assertThrows(EntityValidationException.class, () -> service.findById(0L));

        verify(traineeDao, never()).findById(any());
    }

    @Test
    void findAll_returnsAllTrainees() {
        List<Trainee> trainees = List.of(trainee);
        when(traineeDao.findAll()).thenReturn(trainees);

        List<Trainee> actual = service.findAll();

        assertEquals(1, actual.size());
        assertTrue(actual.contains(trainee));
    }

    @Test
    void findAll_whenEmpty_returnsEmptyList() {
        when(traineeDao.findAll()).thenReturn(List.of());

        List<Trainee> actual = service.findAll();

        assertTrue(actual.isEmpty());
    }

    private Trainee buildTrainee() {
        User user = User.builder()
                .id(ID)
                .firstName("Abdul")
                .lastName("Hariton")
                .username("Abdul.Hariton")
                .isActive(true)
                .build();

        return Trainee.builder()
                .id(ID)
                .user(user)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Kyiv")
                .build();
    }
}