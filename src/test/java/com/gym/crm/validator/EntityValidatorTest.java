package com.gym.crm.validator;

import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityValidatorTest {

    private static final Long EXISTING_ID = 1L;

    private EntityValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EntityValidator();
    }

    @Test
    void validateTrainee_whenValid_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validateTrainee(validTrainee()));
    }

    @Test
    void validateTrainee_whenNull_throwsWithCorrectMessage() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTrainee(null));

        assertEquals("Trainee cannot be null", ex.getMessage());
    }

    @Test
    void validateTrainee_whenFirstNameIsNull_throwsWithCorrectMessage() {
        Trainee trainee = validTrainee().toBuilder()
                .firstName(null)
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTrainee(trainee));

        assertEquals("First name cannot be blank", ex.getMessage());
    }

    @Test
    void validateTrainee_whenFirstNameIsBlank_throwsWithCorrectMessage() {
        Trainee trainee = validTrainee().toBuilder()
                .firstName("   ")
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTrainee(trainee));

        assertEquals("First name cannot be blank", ex.getMessage());
    }

    @Test
    void validateTrainee_whenLastNameIsNull_throwsWithCorrectMessage() {
        Trainee trainee = validTrainee().toBuilder()
                .lastName(null)
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTrainee(trainee));

        assertEquals("Last name cannot be blank", ex.getMessage());
    }

    @Test
    void validateTrainee_whenLastNameIsBlank_throwsWithCorrectMessage() {
        Trainee trainee = validTrainee().toBuilder()
                .lastName("")
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTrainee(trainee));

        assertEquals("Last name cannot be blank", ex.getMessage());
    }

    @Test
    void validateTrainee_whenBothNamesAreNull_throwsOnFirstName() {
        Trainee trainee = validTrainee().toBuilder()
                .firstName(null)
                .lastName(null)
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTrainee(trainee));

        assertEquals("First name cannot be blank", ex.getMessage());
    }

    @Test
    void validateTrainer_whenValid_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validateTrainer(validTrainer()));
    }

    @Test
    void validateTrainer_whenNull_throwsWithCorrectMessage() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTrainer(null));

        assertEquals("Trainer cannot be null", ex.getMessage());
    }

    @Test
    void validateTrainer_whenFirstNameIsBlank_throwsWithCorrectMessage() {
        Trainer trainer = validTrainer().toBuilder()
                .firstName("")
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTrainer(trainer));

        assertEquals("First name cannot be blank", ex.getMessage());
    }

    @Test
    void validateTrainer_whenLastNameIsBlank_throwsWithCorrectMessage() {
        Trainer trainer = validTrainer().toBuilder()
                .lastName("  ")
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTrainer(trainer));

        assertEquals("Last name cannot be blank", ex.getMessage());
    }

    @Test
    void validateTrainer_whenBothNamesAreBlank_throwsOnFirstName() {
        Trainer trainer = validTrainer().toBuilder()
                .firstName("")
                .lastName("")
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTrainer(trainer));

        assertEquals("First name cannot be blank", ex.getMessage());
    }

    @Test
    void validateTraining_whenValid_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validateTraining(validTraining()));
    }

    @Test
    void validateTraining_whenNull_throwsWithCorrectMessage() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTraining(null));

        assertEquals("Training cannot be null", ex.getMessage());
    }

    @Test
    void validateTraining_whenTrainingNameIsBlank_throwsWithCorrectMessage() {
        Training training = validTraining().toBuilder()
                .trainingName("  ")
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTraining(training));

        assertEquals("Training name cannot be blank", ex.getMessage());
    }

    @Test
    void validateTraining_whenTraineeIdIsNull_throwsValidationException() {
        Training training = validTraining().toBuilder()
                .traineeId(null)
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTraining(training));

        assertEquals("Id must be a positive integer, but was null", ex.getMessage());
    }

    @Test
    void validateTraining_whenTrainerIdIsZero_throwsValidationException() {
        Training training = validTraining().toBuilder()
                .trainerId(0L)
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTraining(training));

        assertEquals("Id must be a positive integer, but was 0", ex.getMessage());
    }

    @Test
    void validateTraining_whenTrainingDateIsNull_throwsWithCorrectMessage() {
        Training training = validTraining().toBuilder()
                .trainingDate(null)
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTraining(training));

        assertEquals("Training date cannot be null", ex.getMessage());
    }

    @Test
    void validateTraining_whenTrainingTypeIsNull_throwsWithCorrectMessage() {
        Training training = validTraining().toBuilder()
                .trainingType(null)
                .build();

        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateTraining(training));

        assertEquals("Training type cannot be null", ex.getMessage());
    }

    @Test
    void requireValidId_whenPositive_doesNotThrow() {
        assertDoesNotThrow(() -> validator.requireValidId(EXISTING_ID));
    }

    @Test
    void requireValidId_whenNull_throwsValidationException() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.requireValidId(null));

        assertEquals("Id must be a positive integer, but was null", ex.getMessage());
    }

    @Test
    void requireValidId_whenZero_throwsValidationException() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.requireValidId(0L));

        assertEquals("Id must be a positive integer, but was 0", ex.getMessage());
    }

    @Test
    void requireValidId_whenNegative_throwsValidationException() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.requireValidId(-5L));

        assertEquals("Id must be a positive integer, but was -5", ex.getMessage());
    }

    @Test
    void validateForUpdate_whenEntityAndIdAreValid_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validateForUpdate(new Object(), EXISTING_ID));
    }

    @Test
    void validateForUpdate_whenEntityIsNull_throwsWithCorrectMessage() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateForUpdate(null, EXISTING_ID));

        assertEquals("Entity cannot be null", ex.getMessage());
    }

    @Test
    void validateForUpdate_whenIdIsNull_throwsValidationException() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateForUpdate(new Object(), null));

        assertEquals("Id must be a positive integer, but was null", ex.getMessage());
    }

    @Test
    void validateForUpdate_whenIdIsZero_throwsValidationException() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateForUpdate(new Object(), 0L));

        assertEquals("Id must be a positive integer, but was 0", ex.getMessage());
    }

    @Test
    void validateForUpdate_whenIdIsNegative_throwsValidationException() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> validator.validateForUpdate(new Object(), -EXISTING_ID));

        assertEquals("Id must be a positive integer, but was -1", ex.getMessage());
    }

    private Trainee validTrainee() {
        return Trainee.builder()
                .firstName("Abdul")
                .lastName("Hariton")
                .build();
    }

    private Trainer validTrainer() {
        return Trainer.builder()
                .firstName("Mike")
                .lastName("Tyson")
                .build();
    }

    private Training validTraining() {
        return Training.builder()
                .trainingName("Boxing basics")
                .traineeId(EXISTING_ID)
                .trainerId(EXISTING_ID)
                .trainingDate(LocalDate.now())
                .trainingType(new TrainingType("BOXING"))
                .build();
    }
}