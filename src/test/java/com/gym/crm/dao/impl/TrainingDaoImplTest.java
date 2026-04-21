package com.gym.crm.dao.impl;

import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingDaoImplTest {

    @Mock
    private InMemoryStorage inMemoryStorage;

    @InjectMocks
    private TrainingDaoImpl trainingDao;

    private static final Long EXISTING_ID = 1L;
    private static final Long SECOND_ID = 2L;
    private static final Long GAP_ID = 5L;
    private static final Long GENERATED_ID = 6L;
    private static final Long NON_EXISTING_ID = 99L;

    private Map<Long, Training> storageMap;
    private Training training;

    @BeforeEach
    void setUp() {
        storageMap = new HashMap<>();
        training = buildTraining();

        when(inMemoryStorage.getTrainingStorage()).thenReturn(storageMap);
    }

    @Test
    void save_whenTrainingHasId_storesWithProvidedId() {
        Training actual = trainingDao.save(training);

        assertEquals(EXISTING_ID, actual.getTrainingId());
        assertEquals(training, storageMap.get(EXISTING_ID));
    }

    @Test
    void save_whenTrainingHasNoIdAndStorageIsEmpty_generatesIdOne() {
        Training trainingWithoutId = training.toBuilder()
                .trainingId(null)
                .build();

        Training actual = trainingDao.save(trainingWithoutId);

        assertEquals(EXISTING_ID, actual.getTrainingId());
        assertTrue(storageMap.containsKey(EXISTING_ID));
    }

    @Test
    void save_whenTrainingHasNoIdAndStorageHasEntries_generatesNextSequentialId() {
        storageMap.put(EXISTING_ID, training);
        Training second = training.toBuilder()
                .trainingId(null)
                .trainingName("Yoga basics")
                .build();

        Training actual = trainingDao.save(second);

        assertEquals(SECOND_ID, actual.getTrainingId());
        assertTrue(storageMap.containsKey(SECOND_ID));
    }

    @Test
    void save_whenStorageHasGapsInIds_generatesIdAsMaxPlusOne() {
        storageMap.put(SECOND_ID, training.toBuilder().trainingId(SECOND_ID).build());
        storageMap.put(GAP_ID, training.toBuilder().trainingId(GAP_ID).build());
        Training newTraining = training.toBuilder().trainingId(null).build();

        Training actual = trainingDao.save(newTraining);

        assertEquals(GENERATED_ID, actual.getTrainingId());
        assertTrue(storageMap.containsKey(GENERATED_ID));
    }

    @Test
    void findById_whenTrainingExists_returnsTraining() {
        storageMap.put(EXISTING_ID, training);

        Optional<Training> actual = trainingDao.findById(EXISTING_ID);

        assertTrue(actual.isPresent());
        assertEquals(training, actual.get());
    }

    @Test
    void findById_whenTrainingNotExists_returnsEmpty() {
        Optional<Training> actual = trainingDao.findById(NON_EXISTING_ID);

        assertFalse(actual.isPresent());
    }

    @Test
    void findAll_whenStorageHasEntries_returnsAllTrainings() {
        Training second = training.toBuilder()
                .trainingId(SECOND_ID)
                .trainingName("Yoga basics")
                .build();
        storageMap.put(EXISTING_ID, training);
        storageMap.put(SECOND_ID, second);

        List<Training> actual = trainingDao.findAll();

        assertEquals(2, actual.size());
        assertTrue(actual.contains(training));
        assertTrue(actual.contains(second));
    }

    @Test
    void findAll_whenStorageIsEmpty_returnsEmptyList() {
        List<Training> actual = trainingDao.findAll();

        assertTrue(actual.isEmpty());
    }

    @Test
    void findAll_returnsImmutableList() {
        storageMap.put(EXISTING_ID, training);
        List<Training> actual = trainingDao.findAll();

        assertThrows(UnsupportedOperationException.class, () -> actual.add(training));
    }

    private Training buildTraining() {
        return Training.builder()
                .trainingId(EXISTING_ID)
                .traineeId(EXISTING_ID)
                .trainerId(EXISTING_ID)
                .trainingName("Boxing basics")
                .trainingType(new TrainingType("BOXING"))
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(60)
                .build();
    }
}