package com.gym.crm.dao.impl;

import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainee;
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
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class TraineeDaoImplTest {

    private static final Long EXISTING_ID = 1L;
    private static final Long SECOND_ID = 2L;
    private static final Long GAP_ID = 5L;
    private static final Long GENERATED_ID = 6L;
    private static final Long NON_EXISTING_ID = 99L;

    @Mock
    private InMemoryStorage inMemoryStorage;
    @InjectMocks
    private TraineeDaoImpl traineeDao;

    private Map<Long, Trainee> storageMap;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        storageMap = new HashMap<>();
        trainee = buildTrainee(EXISTING_ID, "Abdul", "Kyiv");

        doReturn(storageMap).when(inMemoryStorage).getEntityStorage("trainees");
    }

    @Test
    void save_whenTraineeHasId_storesWithProvidedId() {
        Trainee actual = traineeDao.save(trainee);

        assertEquals(EXISTING_ID, actual.getUserId());
        assertEquals(trainee, storageMap.get(EXISTING_ID));
    }

    @Test
    void save_whenTraineeHasNoIdAndStorageIsEmpty_generatesIdOne() {
        Trainee traineeWithoutId = trainee.toBuilder()
                .userId(null)
                .build();

        Trainee actual = traineeDao.save(traineeWithoutId);

        assertEquals(EXISTING_ID, actual.getUserId());
        assertTrue(storageMap.containsKey(EXISTING_ID));
    }

    @Test
    void save_whenTraineeHasNoIdAndStorageHasEntries_generatesNextSequentialId() {
        storageMap.put(EXISTING_ID, trainee);
        Trainee second = trainee.toBuilder()
                .userId(null)
                .firstName("Jane")
                .build();

        Trainee actual = traineeDao.save(second);

        assertEquals(SECOND_ID, actual.getUserId());
        assertTrue(storageMap.containsKey(SECOND_ID));
    }

    @Test
    void save_whenStorageHasGapsInIds_generatesIdAsMaxPlusOne() {
        storageMap.put(SECOND_ID, trainee.toBuilder().userId(SECOND_ID).build());
        storageMap.put(GAP_ID, trainee.toBuilder().userId(GAP_ID).build());
        Trainee newTrainee = trainee.toBuilder().userId(null).build();

        Trainee actual = traineeDao.save(newTrainee);

        assertEquals(GENERATED_ID, actual.getUserId());
        assertTrue(storageMap.containsKey(GENERATED_ID));
    }

    @Test
    void update_whenTraineeExists_updatesAndReturns() {
        storageMap.put(EXISTING_ID, trainee);
        Trainee updated = trainee.toBuilder()
                .address("Lviv")
                .build();

        Trainee actual = traineeDao.update(updated);

        assertEquals("Lviv", actual.getAddress());
        assertEquals("Lviv", storageMap.get(EXISTING_ID).getAddress());
    }

    @Test
    void update_whenTraineeNotExists_throwsEntityNotFoundException() {
        Trainee missing = trainee.toBuilder().userId(NON_EXISTING_ID).build();

        assertThrows(EntityNotFoundException.class, () -> traineeDao.update(missing));
    }

    @Test
    void delete_whenTraineeExists_removesFromStorage() {
        storageMap.put(EXISTING_ID, trainee);

        traineeDao.delete(EXISTING_ID);

        assertFalse(storageMap.containsKey(EXISTING_ID));
    }

    @Test
    void delete_whenTraineeNotExists_throwsEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> traineeDao.delete(NON_EXISTING_ID));
    }

    @Test
    void findById_whenTraineeExists_returnsTrainee() {
        storageMap.put(EXISTING_ID, trainee);

        Optional<Trainee> actual = traineeDao.findById(EXISTING_ID);

        assertTrue(actual.isPresent());
        assertEquals(trainee, actual.get());
    }

    @Test
    void findById_whenTraineeNotExists_returnsEmpty() {
        Optional<Trainee> actual = traineeDao.findById(NON_EXISTING_ID);

        assertFalse(actual.isPresent());
    }

    @Test
    void findAll_whenStorageHasEntries_returnsAllTrainees() {
        Trainee second = trainee.toBuilder()
                .userId(SECOND_ID)
                .firstName("Jane")
                .build();
        storageMap.put(EXISTING_ID, trainee);
        storageMap.put(SECOND_ID, second);

        List<Trainee> actual = traineeDao.findAll();

        assertEquals(2, actual.size());
        assertTrue(actual.contains(trainee));
        assertTrue(actual.contains(second));
    }

    @Test
    void findAll_whenStorageIsEmpty_returnsEmptyList() {
        List<Trainee> actual = traineeDao.findAll();

        assertTrue(actual.isEmpty());
    }

    @Test
    void findAll_returnsImmutableList() {
        storageMap.put(EXISTING_ID, trainee);
        List<Trainee> actual = traineeDao.findAll();

        assertThrows(UnsupportedOperationException.class, () -> actual.add(trainee));
    }

    private Trainee buildTrainee(Long id, String firstName, String address) {
        return Trainee.builder()
                .userId(id)
                .firstName(firstName)
                .lastName("Hariton")
                .username("Abdul.Hariton")
                .password("hashedPassword")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address(address)
                .isActive(true)
                .build();
    }
}