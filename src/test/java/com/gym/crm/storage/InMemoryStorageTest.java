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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryStorageTest {

    @Mock
    private StorageInitializer initializer;

    private static final Long ID = 1L;
    private static final Long SECOND_ID = 2L;


    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Training> trainingStorage;
    private InMemoryStorage inMemoryStorage;

    @BeforeEach
    void setUp() {
        traineeStorage = new HashMap<>();
        trainerStorage = new HashMap<>();
        trainingStorage = new HashMap<>();

        inMemoryStorage = new InMemoryStorage();
        inMemoryStorage.setTraineeStorage(traineeStorage);
        inMemoryStorage.setTrainerStorage(trainerStorage);
        inMemoryStorage.setTrainingStorage(trainingStorage);
        inMemoryStorage.setInitializer(initializer);
    }

    @Test
    void getTraineeStorage_returnsInjectedMap() {
        assertSame(traineeStorage, inMemoryStorage.getTraineeStorage());
    }

    @Test
    void getTrainerStorage_returnsInjectedMap() {
        assertSame(trainerStorage, inMemoryStorage.getTrainerStorage());
    }

    @Test
    void getTrainingStorage_returnsInjectedMap() {
        assertSame(trainingStorage, inMemoryStorage.getTrainingStorage());
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