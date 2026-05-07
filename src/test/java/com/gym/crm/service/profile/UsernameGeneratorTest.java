package com.gym.crm.service.profile;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameGeneratorTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private UsernameGenerator usernameGenerator;

    @Test
    void generate_whenNoUsernameExists_returnsBaseUsername() {
        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of());

        String actual = usernameGenerator.generate("Abdul", "Hariton");

        assertEquals("Abdul.Hariton", actual);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    void generate_whenBaseUsernameExistsInTrainees_returnsUsernameWithSuffix1() {
        when(traineeDao.findAll()).thenReturn(List.of(traineeWithUsername("Abdul.Hariton")));
        when(trainerDao.findAll()).thenReturn(List.of());

        String actual = usernameGenerator.generate("Abdul", "Hariton");

        assertEquals("Abdul.Hariton1", actual);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    void generate_whenSuffix1AlsoExistsInTrainees_returnsUsernameWithSuffix2() {
        when(traineeDao.findAll()).thenReturn(List.of(
                traineeWithUsername("Abdul.Hariton"),
                traineeWithUsername("Abdul.Hariton1")));
        when(trainerDao.findAll()).thenReturn(List.of());

        String actual = usernameGenerator.generate("Abdul", "Hariton");

        assertEquals("Abdul.Hariton2", actual);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    void generate_whenBaseUsernameExistsInTrainers_returnsUsernameWithSuffix1() {
        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of(trainerWithUsername("Abdul.Hariton")));

        String actual = usernameGenerator.generate("Abdul", "Hariton");

        assertEquals("Abdul.Hariton1", actual);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    void generate_whenBaseUsernameExistsInBoth_returnsUsernameWithSuffix1() {
        when(traineeDao.findAll()).thenReturn(List.of(traineeWithUsername("Abdul.Hariton")));
        when(trainerDao.findAll()).thenReturn(List.of(trainerWithUsername("Abdul.Hariton1")));

        String actual = usernameGenerator.generate("Abdul", "Hariton");

        assertEquals("Abdul.Hariton2", actual);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    void generate_whenMultipleSuffixesExistAcrossBoth_returnsNextAvailableSuffix() {
        when(traineeDao.findAll()).thenReturn(List.of(
                traineeWithUsername("Abdul.Hariton"),
                traineeWithUsername("Abdul.Hariton1"),
                traineeWithUsername("Abdul.Hariton2")));
        when(trainerDao.findAll()).thenReturn(List.of(
                trainerWithUsername("Abdul.Hariton3")));

        String actual = usernameGenerator.generate("Abdul", "Hariton");

        assertEquals("Abdul.Hariton4", actual);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    void generate_whenStorageContainsNullUsernames_ignoresThemAndReturnsBaseUsername() {
        when(traineeDao.findAll()).thenReturn(List.of(traineeWithUsername(null)));
        when(trainerDao.findAll()).thenReturn(List.of());

        String actual = usernameGenerator.generate("Abdul", "Hariton");

        assertEquals("Abdul.Hariton", actual);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    void generate_whenDifferentNameExists_returnsBaseUsernameWithoutSuffix() {
        when(traineeDao.findAll()).thenReturn(List.of(traineeWithUsername("Mike.Tyson")));
        when(trainerDao.findAll()).thenReturn(List.of());

        String actual = usernameGenerator.generate("Abdul", "Hariton");

        assertEquals("Abdul.Hariton", actual);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    private Trainee traineeWithUsername(String username) {
        User user = User.builder()
                .username(username)
                .firstName("Abdul")
                .lastName("Hariton")
                .build();
        return Trainee.builder()
                .user(user)
                .build();
    }

    private Trainer trainerWithUsername(String username) {
        User user = User.builder()
                .username(username)
                .firstName("Abdul")
                .lastName("Hariton")
                .build();
        return Trainer.builder()
                .user(user)
                .specialization(TrainingType.builder().trainingTypeName("BOXING").build())
                .build();
    }
}