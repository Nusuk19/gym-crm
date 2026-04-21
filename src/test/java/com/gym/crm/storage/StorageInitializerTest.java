package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.parser.CsvFileParser;
import com.gym.crm.reader.FileLineReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageInitializerTest {

    private static final String TRAINEE_LINE =
            "TRAINEE,1,Abdul,Hariton,Abdul.Hariton,hashedPass,1990-01-01,Kyiv,true";
    private static final String TRAINER_LINE =
            "TRAINER,1,Mike,Tyson,Mike.Tyson,hashedPass,BOXING,true";
    private static final String TRAINING_LINE =
            "TRAINING,1,1,1,Boxing basics,BOXING,2024-05-01,60";
    private static final Long ID = 1L;

    @Mock
    private FileLineReader fileLineReader;

    private StorageInitializer storageInitializer;
    private Resource initFile;

    @BeforeEach
    void setUp() throws Exception {
        initFile = mock(Resource.class);

        storageInitializer = new StorageInitializer();
        storageInitializer.setFileLineReader(fileLineReader);
        storageInitializer.setParser(new CsvFileParser());

        Field initFileField = StorageInitializer.class.getDeclaredField("initFile");
        initFileField.setAccessible(true);
        initFileField.set(storageInitializer, initFile);
    }

    @Test
    void loadTrainees_whenFileContainsTraineeLine_returnsTraineeMap() {
        when(fileLineReader.readLines(initFile)).thenReturn(List.of(TRAINEE_LINE));

        Map<Long, Trainee> actual = storageInitializer.loadTrainees();

        assertEquals(1, actual.size());
        Trainee trainee = actual.get(ID);
        assertEquals(ID, trainee.getUserId());
        assertEquals("Abdul", trainee.getFirstName());
        assertEquals("Hariton", trainee.getLastName());
        assertEquals("Abdul.Hariton", trainee.getUsername());
        assertEquals("Kyiv", trainee.getAddress());
        assertTrue(trainee.isActive());
    }

    @Test
    void loadTrainees_whenFileContainsOnlyNonTraineeLines_returnsEmptyMap() {
        when(fileLineReader.readLines(initFile)).thenReturn(List.of(TRAINER_LINE, TRAINING_LINE));

        Map<Long, Trainee> actual = storageInitializer.loadTrainees();

        assertTrue(actual.isEmpty());
    }

    @Test
    void loadTrainees_whenFileContainsMixedLines_returnsOnlyTrainees() {
        when(fileLineReader.readLines(initFile))
                .thenReturn(List.of(TRAINEE_LINE, TRAINER_LINE, TRAINING_LINE));

        Map<Long, Trainee> actual = storageInitializer.loadTrainees();

        assertEquals(1, actual.size());
        assertTrue(actual.containsKey(ID));
    }

    @Test
    void loadTrainees_whenFileIsEmpty_returnsEmptyMap() {
        when(fileLineReader.readLines(initFile)).thenReturn(List.of());

        Map<Long, Trainee> actual = storageInitializer.loadTrainees();

        assertTrue(actual.isEmpty());
    }

    @Test
    void loadTrainees_whenLineHasNoComma_throwsIllegalStateException() {
        when(fileLineReader.readLines(initFile)).thenReturn(List.of("INVALID_LINE_WITHOUT_COMMA"));

        assertThrows(IllegalStateException.class, () -> storageInitializer.loadTrainees());
    }

    @Test
    void loadTrainees_whenLineHasUnknownRecordType_throwsIllegalStateException() {
        when(fileLineReader.readLines(initFile)).thenReturn(List.of("UNKNOWN,1,data"));

        assertThrows(IllegalStateException.class, () -> storageInitializer.loadTrainees());
    }

    @Test
    void loadTrainers_whenFileContainsTrainerLine_returnsTrainerMap() {
        when(fileLineReader.readLines(initFile)).thenReturn(List.of(TRAINER_LINE));

        Map<Long, Trainer> actual = storageInitializer.loadTrainers();

        assertEquals(1, actual.size());
        Trainer trainer = actual.get(ID);
        assertEquals(ID, trainer.getUserId());
        assertEquals("Mike", trainer.getFirstName());
        assertEquals("Tyson", trainer.getLastName());
        assertEquals("Mike.Tyson", trainer.getUsername());
        assertEquals("BOXING", trainer.getSpecialization().getTrainingTypeName());
        assertTrue(trainer.isActive());
    }

    @Test
    void loadTrainers_whenFileContainsOnlyNonTrainerLines_returnsEmptyMap() {
        when(fileLineReader.readLines(initFile)).thenReturn(List.of(TRAINEE_LINE, TRAINING_LINE));

        Map<Long, Trainer> actual = storageInitializer.loadTrainers();

        assertTrue(actual.isEmpty());
    }

    @Test
    void loadTrainers_whenFileContainsMixedLines_returnsOnlyTrainers() {
        when(fileLineReader.readLines(initFile))
                .thenReturn(List.of(TRAINEE_LINE, TRAINER_LINE, TRAINING_LINE));

        Map<Long, Trainer> actual = storageInitializer.loadTrainers();

        assertEquals(1, actual.size());
        assertTrue(actual.containsKey(ID));
    }

    @Test
    void loadTrainings_whenFileContainsTrainingLine_returnsTrainingMap() {
        when(fileLineReader.readLines(initFile)).thenReturn(List.of(TRAINING_LINE));

        Map<Long, Training> actual = storageInitializer.loadTrainings();

        assertEquals(1, actual.size());
        Training training = actual.get(ID);
        assertEquals(ID, training.getTrainingId());
        assertEquals(ID, training.getTraineeId());
        assertEquals(ID, training.getTrainerId());
        assertEquals("Boxing basics", training.getTrainingName());
        assertEquals("BOXING", training.getTrainingType().getTrainingTypeName());
        assertEquals(60, training.getTrainingDuration());
    }

    @Test
    void loadTrainings_whenFileContainsOnlyNonTrainingLines_returnsEmptyMap() {
        when(fileLineReader.readLines(initFile)).thenReturn(List.of(TRAINEE_LINE, TRAINER_LINE));

        Map<Long, Training> actual = storageInitializer.loadTrainings();

        assertTrue(actual.isEmpty());
    }

    @Test
    void loadTrainings_whenFileContainsMixedLines_returnsOnlyTrainings() {
        when(fileLineReader.readLines(initFile))
                .thenReturn(List.of(TRAINEE_LINE, TRAINER_LINE, TRAINING_LINE));

        Map<Long, Training> actual = storageInitializer.loadTrainings();

        assertEquals(1, actual.size());
        assertTrue(actual.containsKey(ID));
    }
}