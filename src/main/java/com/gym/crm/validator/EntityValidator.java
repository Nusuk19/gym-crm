package com.gym.crm.validator;

import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.springframework.stereotype.Component;

@Component
public class EntityValidator {

    public void validateTrainee(Trainee trainee) {
        requireNonNull(trainee, "Trainee cannot be null");
        requireNonBlank(trainee.getFirstName(), "First name cannot be blank");
        requireNonBlank(trainee.getLastName(), "Last name cannot be blank");
    }

    public void validateTrainer(Trainer trainer) {
        requireNonNull(trainer, "Trainer cannot be null");
        requireNonBlank(trainer.getFirstName(), "First name cannot be blank");
        requireNonBlank(trainer.getLastName(), "Last name cannot be blank");
    }

    public void validateTraining(Training training) {
        requireNonNull(training, "Training cannot be null");
        requireNonBlank(training.getTrainingName(), "Training name cannot be blank");
        requireValidId(training.getTraineeId());
        requireValidId(training.getTrainerId());
        requireNonNull(training.getTrainingDate(), "Training date cannot be null");
        requireNonNull(training.getTrainingType(), "Training type cannot be null");
    }

    public void validateForUpdate(Object entity, Long id) {
        requireNonNull(entity, "Entity cannot be null");
        requireValidId(id);
    }

    public void requireValidId(Long id) {
        if (id == null || id <= 0) {
            throw new EntityValidationException("Id must be a positive integer, but was " + id);
        }
    }

    private void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new EntityValidationException(message);
        }
    }

    private void requireNonNull(Object value, String message) {
        if (value == null) {
            throw new EntityValidationException(message);
        }
    }
}