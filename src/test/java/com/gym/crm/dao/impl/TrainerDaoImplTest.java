package com.gym.crm.dao.impl;

import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class TrainerDaoImplTest {

    @Mock
    private InMemoryStorage inMemoryStorage;

    @InjectMocks
    private TrainerDaoImpl trainerDao;

    private static final Long EXISTING_ID = 1L;
    private static final Long SECOND_ID = 2L;
    private static final Long GAP_ID = 5L;
    private static final Long GENERATED_ID = 6L;
    private static final Long NON_EXISTING_ID = 99L;

    private Map<Long, Trainer> storageMap;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        storageMap = new HashMap<>();
        trainer = buildTrainer();

        when(inMemoryStorage.getTrainerStorage()).thenReturn(storageMap);
    }

    @Test
    void save_whenTrainerHasId_storesWithProvidedId() {
        Trainer actual = trainerDao.save(trainer);

        assertEquals(EXISTING_ID, actual.getUserId());
        assertEquals(trainer, storageMap.get(EXISTING_ID));
    }

    @Test
    void save_whenTrainerHasNoIdAndStorageIsEmpty_generatesIdOne() {
        Trainer trainerWithoutId = trainer.toBuilder()
                .userId(null)
                .build();

        Trainer actual = trainerDao.save(trainerWithoutId);

        assertEquals(EXISTING_ID, actual.getUserId());
        assertTrue(storageMap.containsKey(EXISTING_ID));
    }

    @Test
    void save_whenTrainerHasNoIdAndStorageHasEntries_generatesNextSequentialId() {
        storageMap.put(EXISTING_ID, trainer);
        Trainer second = trainer.toBuilder()
                .userId(null)
                .firstName("Bruce")
                .build();

        Trainer actual = trainerDao.save(second);

        assertEquals(SECOND_ID, actual.getUserId());
        assertTrue(storageMap.containsKey(SECOND_ID));
    }

    @Test
    void save_whenStorageHasGapsInIds_generatesIdAsMaxPlusOne() {
        storageMap.put(SECOND_ID, trainer.toBuilder().userId(SECOND_ID).build());
        storageMap.put(GAP_ID, trainer.toBuilder().userId(GAP_ID).build());
        Trainer newTrainer = trainer.toBuilder().userId(null).build();

        Trainer actual = trainerDao.save(newTrainer);

        assertEquals(GENERATED_ID, actual.getUserId());
        assertTrue(storageMap.containsKey(GENERATED_ID));
    }

    @Test
    void update_whenTrainerExists_updatesAndReturns() {
        storageMap.put(EXISTING_ID, trainer);
        Trainer updated = trainer.toBuilder()
                .specialization(new TrainingType("YOGA"))
                .build();

        Trainer actual = trainerDao.update(updated);

        assertEquals("YOGA", actual.getSpecialization().getTrainingTypeName());
        assertEquals("YOGA", storageMap.get(EXISTING_ID).getSpecialization().getTrainingTypeName());
    }

    @Test
    void update_whenTrainerNotExists_throwsEntityNotFoundException() {
        Trainer missing = trainer.toBuilder().userId(NON_EXISTING_ID).build();

        assertThrows(EntityNotFoundException.class, () -> trainerDao.update(missing));
    }

    @Test
    void findById_whenTrainerExists_returnsTrainer() {
        storageMap.put(EXISTING_ID, trainer);

        Optional<Trainer> actual = trainerDao.findById(EXISTING_ID);

        assertTrue(actual.isPresent());
        assertEquals(trainer, actual.get());
    }

    @Test
    void findById_whenTrainerNotExists_returnsEmpty() {
        Optional<Trainer> actual = trainerDao.findById(NON_EXISTING_ID);

        assertFalse(actual.isPresent());
    }

    @Test
    void findAll_whenStorageHasEntries_returnsAllTrainers() {
        Trainer second = trainer.toBuilder()
                .userId(SECOND_ID)
                .firstName("Bruce")
                .build();
        storageMap.put(EXISTING_ID, trainer);
        storageMap.put(SECOND_ID, second);

        List<Trainer> actual = trainerDao.findAll();

        assertEquals(2, actual.size());
        assertTrue(actual.contains(trainer));
        assertTrue(actual.contains(second));
    }

    @Test
    void findAll_whenStorageIsEmpty_returnsEmptyList() {
        List<Trainer> actual = trainerDao.findAll();

        assertTrue(actual.isEmpty());
    }

    @Test
    void findAll_returnsImmutableList() {
        storageMap.put(EXISTING_ID, trainer);
        List<Trainer> actual = trainerDao.findAll();

        assertThrows(UnsupportedOperationException.class, () -> actual.add(trainer));
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .userId(EXISTING_ID)
                .firstName("Mike")
                .lastName("Tyson")
                .username("Mike.Tyson")
                .password("hashedPassword")
                .specialization(new TrainingType("BOXING"))
                .isActive(true)
                .build();
    }
}