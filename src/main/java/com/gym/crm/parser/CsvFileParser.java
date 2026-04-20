package com.gym.crm.parser;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CsvFileParser {

    private static final String DELIMITER = ",";

    public Trainee parseTrainee(String line) {
        String[] tokens = line.split(DELIMITER);
        if (tokens.length < 9) {
            throw new IllegalArgumentException(
                    String.format(
                            "Invalid TRAINEE record. Expected 9 fields but got %d. Line: [%s]",
                            tokens.length,
                            line
                    )
            );
        }

        return Trainee.builder()
                .userId(Long.parseLong(tokens[1]))
                .firstName(tokens[2])
                .lastName(tokens[3])
                .username(tokens[4])
                .password(tokens[5])
                .dateOfBirth(LocalDate.parse(tokens[6]))
                .address(tokens[7])
                .isActive(Boolean.parseBoolean(tokens[8]))
                .build();
    }

    public Trainer parseTrainer(String line) {
        String[] tokens = line.split(DELIMITER);
        if (tokens.length < 8) {
            throw new IllegalArgumentException(
                    String.format(
                            "Invalid TRAINER record. Expected 8 fields but got %d. Line: [%s]",
                            tokens.length,
                            line
                    )
            );
        }

        return Trainer.builder()
                .userId(Long.parseLong(tokens[1]))
                .firstName(tokens[2])
                .lastName(tokens[3])
                .username(tokens[4])
                .password(tokens[5])
                .specialization(new TrainingType(tokens[6]))
                .isActive(Boolean.parseBoolean(tokens[7]))
                .build();
    }

    public Training parseTraining(String line) {
        String[] tokens = line.split(DELIMITER);
        if (tokens.length < 8) {
            throw new IllegalArgumentException(
                    String.format(
                            "Invalid TRAINING record. Expected 8 fields but got %d. Line: [%s]",
                            tokens.length,
                            line
                    )
            );
        }

        return Training.builder()
                .trainingId(Long.parseLong(tokens[1]))
                .traineeId(Long.parseLong(tokens[2]))
                .trainerId(Long.parseLong(tokens[3]))
                .trainingName(tokens[4])
                .trainingType(new TrainingType(tokens[5]))
                .trainingDate(LocalDate.parse(tokens[6]))
                .trainingDuration(Integer.parseInt(tokens[7]))
                .build();
    }
}