package com.gym.crm.service.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
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
    void create_whenValidTrainee_logsInfoWithUsername() {
        when(userProfileService.generateUsername("Abdul", "Hariton")).thenReturn("Abdul.Hariton");
        when(userProfileService.generatePassword()).thenReturn("rawPass123");
        when(userProfileService.hashPassword("rawPass123")).thenReturn("hashedPass");
        when(traineeDao.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(trainee);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getLevel)
                .doesNotContain(Level.ERROR);

        assertThat(listAppender.list)
                .anySatisfy(event -> {
                    assertThat(event.getLevel()).isEqualTo(Level.INFO);
                    assertThat(event.getFormattedMessage())
                            .contains("Creating trainee")
                            .contains("Abdul")
                            .contains("Hariton");
                });
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
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee actual = service.update(trainee);

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

        assertThrows(EntityValidationException.class, () -> service.update(invalidTrainee));

        verify(traineeDao, never()).update(any());
    }

    @Test
    void delete_whenValidId_deletesSuccessfully() {
        service.delete(ID);

        verify(validator).requireValidId(ID);
        verify(traineeDao).delete(ID);
    }

    @Test
    void delete_whenInvalidId_throwsException() {
        doThrow(new EntityValidationException("Id must be a positive integer"))
                .when(validator).requireValidId(any());

        assertThrows(EntityValidationException.class, () -> service.delete(-ID));

        verify(traineeDao, never()).delete(any());
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