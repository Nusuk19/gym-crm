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

        return Trainee.builder()
                .userId(Long.parseLong(tokens[1]))
                .firstName(tokens[2])
                .lastName(tokens[3])
                .dateOfBirth(LocalDate.parse(tokens[4]))
                .address(tokens[5])
                .isActive(Boolean.parseBoolean(tokens[6]))
                .build();
    }

    public Trainer parseTrainer(String line) {
        String[] tokens = line.split(DELIMITER);

        return Trainer.builder()
                .userId(Long.parseLong(tokens[1]))
                .firstName(tokens[2])
                .lastName(tokens[3])
                .specialization(new TrainingType(tokens[4]))
                .isActive(Boolean.parseBoolean(tokens[5]))
                .build();
    }

    public Training parseTraining(String line) {
        String[] tokens = line.split(DELIMITER);

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