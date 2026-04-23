package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryStorageTest {

    private static final Long ID = 1L;
    private static final Long SECOND_ID = 2L;

    @Mock
    private StorageInitializer initializer;

    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Training> trainingStorage;

    private InMemoryStorage inMemoryStorage;

    @BeforeEach
    void setUp() {
        Map<String, Map<Long, Object>> storage = new HashMap<>();
        storage.put("trainees", new HashMap<>());
        storage.put("trainers", new HashMap<>());
        storage.put("trainings", new HashMap<>());

        inMemoryStorage = new InMemoryStorage();
        inMemoryStorage.setStorage(storage);
        inMemoryStorage.setInitializer(initializer);

        traineeStorage = inMemoryStorage.getEntityStorage("trainees");
        trainerStorage = inMemoryStorage.getEntityStorage("trainers");
        trainingStorage = inMemoryStorage.getEntityStorage("trainings");
    }

    @Test
    void getTraineeStorage_returnsInjectedMap() {
        Map<Long, Trainee> result = inMemoryStorage.getEntityStorage("trainees");

        assertNotNull(result);
    }

    @Test
    void getTrainerStorage_returnsInjectedMap() {
        Map<Long, Trainer> result = inMemoryStorage.getEntityStorage("trainers");

        assertNotNull(result);
    }

    @Test
    void getTrainingStorage_returnsInjectedMap() {
        Map<Long, Training> result = inMemoryStorage.getEntityStorage("trainings");

        assertNotNull(result);
    }

    @Test
    void init_populatesTraineeStorageFromInitializer() {
        Trainee trainee = Trainee.builder()
                .userId(ID)
                .firstName("Abdul")
                .lastName("Hariton")
                .build();
        when(initializer.loadTrainees()).thenReturn(Map.of(ID, trainee));
        when(initializer.loadTrainers()).thenReturn(Map.of());
        when(initializer.loadTrainings()).thenReturn(Map.of());

        inMemoryStorage.init();

        assertEquals(1, traineeStorage.size());
        assertEquals(trainee, traineeStorage.get(ID));
    }

    @Test
    void init_populatesTrainerStorageFromInitializer() {
        Trainer trainer = Trainer.builder()
                .userId(ID)
                .firstName("Mike")
                .lastName("Tyson")
                .build();
        when(initializer.loadTrainees()).thenReturn(Map.of());
        when(initializer.loadTrainers()).thenReturn(Map.of(ID, trainer));
        when(initializer.loadTrainings()).thenReturn(Map.of());

        inMemoryStorage.init();

        assertEquals(1, trainerStorage.size());
        assertEquals(trainer, trainerStorage.get(ID));
    }

    @Test
    void init_populatesTrainingStorageFromInitializer() {
        Training training = Training.builder()
                .trainingId(ID)
                .trainingName("Boxing basics")
                .build();
        when(initializer.loadTrainees()).thenReturn(Map.of());
        when(initializer.loadTrainers()).thenReturn(Map.of());
        when(initializer.loadTrainings()).thenReturn(Map.of(ID, training));

        inMemoryStorage.init();

        assertEquals(1, trainingStorage.size());
        assertEquals(training, trainingStorage.get(ID));
    }

    @Test
    void init_whenInitializerReturnsMultipleEntities_allAreStored() {
        Trainee first = Trainee.builder().userId(ID).firstName("Abdul").lastName("Hariton").build();
        Trainee second = Trainee.builder().userId(SECOND_ID).firstName("Zenis").lastName("Haris").build();
        when(initializer.loadTrainees()).thenReturn(Map.of(ID, first, SECOND_ID, second));
        when(initializer.loadTrainers()).thenReturn(Map.of());
        when(initializer.loadTrainings()).thenReturn(Map.of());

        inMemoryStorage.init();

        assertEquals(2, traineeStorage.size());
        assertEquals(first, traineeStorage.get(ID));
        assertEquals(second, traineeStorage.get(SECOND_ID));
    }
}