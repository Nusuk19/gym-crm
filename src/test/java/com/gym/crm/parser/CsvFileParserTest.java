package com.gym.crm.parser;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvFileParserTest {

    private static final Long EXISTING_ID = 1L;

    private CsvFileParser parser;

    @BeforeEach
    void setUp() {
        parser = new CsvFileParser();
    }

    @Test
    void parseTrainee_whenValidLine_returnsCorrectTrainee() {
        String line = "TRAINEE,1,Abdul,Hariton,Abdul.Hariton,hashedPass,1990-01-01,Kyiv,true";

        Trainee actual = parser.parseTrainee(line);

        assertEquals(EXISTING_ID, actual.getUserId());
        assertEquals("Abdul", actual.getFirstName());
        assertEquals("Hariton", actual.getLastName());
        assertEquals("Abdul.Hariton", actual.getUsername());
        assertEquals("hashedPass", actual.getPassword());
        assertEquals(LocalDate.of(1990, 1, 1), actual.getDateOfBirth());
        assertEquals("Kyiv", actual.getAddress());
        assertEquals(true, actual.isActive());
    }

    @Test
    void parseTrainee_whenIsActiveFalse_returnsFalse() {
        String line = "TRAINEE,2,Zenis,Haris,Zenis.Haris,hashedPass,1995-05-20,Lviv,false";

        Trainee actual = parser.parseTrainee(line);

        assertEquals(false, actual.isActive());
    }

    @Test
    void parseTrainee_whenNotEnoughFields_throwsIllegalArgumentException() {
        String line = "TRAINEE,1,Abdul,Hariton";

        assertThrows(IllegalArgumentException.class, () -> parser.parseTrainee(line));
    }

    @Test
    void parseTrainer_whenValidLine_returnsCorrectTrainer() {
        String line = "TRAINER,1,Mike,Tyson,Mike.Tyson,hashedPass,BOXING,true";

        Trainer actual = parser.parseTrainer(line);

        assertEquals(EXISTING_ID, actual.getUserId());
        assertEquals("Mike", actual.getFirstName());
        assertEquals("Tyson", actual.getLastName());
        assertEquals("Mike.Tyson", actual.getUsername());
        assertEquals("hashedPass", actual.getPassword());
        assertEquals("BOXING", actual.getSpecialization().getTrainingTypeName());
        assertEquals(true, actual.isActive());
    }

    @Test
    void parseTrainer_whenIsActiveFalse_returnsFalse() {
        String line = "TRAINER,2,Edik,Kane,Edik.Kane,hashedPass,YOGA,false";

        Trainer actual = parser.parseTrainer(line);

        assertEquals(false, actual.isActive());
    }

    @Test
    void parseTrainer_whenNotEnoughFields_throwsIllegalArgumentException() {
        String line = "TRAINER,1,Mike,Tyson";

        assertThrows(IllegalArgumentException.class, () -> parser.parseTrainer(line));
    }

    @Test
    void parseTraining_whenValidLine_returnsCorrectTraining() {
        String line = "TRAINING,1,1,1,Boxing basics,BOXING,2024-05-01,60";

        Training actual = parser.parseTraining(line);

        assertEquals(EXISTING_ID, actual.getTrainingId());
        assertEquals(EXISTING_ID, actual.getTraineeId());
        assertEquals(EXISTING_ID, actual.getTrainerId());
        assertEquals("Boxing basics", actual.getTrainingName());
        assertEquals("BOXING", actual.getTrainingType().getTrainingTypeName());
        assertEquals(LocalDate.of(2024, 5, 1), actual.getTrainingDate());
        assertEquals(60, actual.getTrainingDuration());
    }

    @Test
    void parseTraining_whenNotEnoughFields_throwsIllegalArgumentException() {
        String line = "TRAINING,1,1,1";

        assertThrows(IllegalArgumentException.class, () -> parser.parseTraining(line));
    }
}