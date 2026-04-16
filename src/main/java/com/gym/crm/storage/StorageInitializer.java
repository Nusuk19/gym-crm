package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Map;

@Component
public class StorageInitializer {

    private static final String DELIMITER = ",";
    private static final String TYPE_TRAINEE = "TRAINEE";
    private static final String TYPE_TRAINER = "TRAINER";
    private static final String TYPE_TRAINING = "TRAINING";

    private Resource initFile;
    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Training> trainingStorage;

    @Value("${storage.init.file}")
    public void setInitFile(Resource initFile) {
        this.initFile = initFile;
    }

    @Autowired
    public void setTraineeStorage(Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainerStorage(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Autowired
    public void setTrainingStorage(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(initFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                processLine(line);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Storage initialization failed", e);
        }
    }

    private void processLine(String line) {
        String[] tokens = line.split(DELIMITER);

        switch (tokens[0]) {
            case TYPE_TRAINEE -> processTrainee(tokens);
            case TYPE_TRAINER -> processTrainer(tokens);
            case TYPE_TRAINING -> processTraining(tokens);
            default ->
                    throw new IllegalStateException("Unknown record type: " + tokens[0] + " in line: " + String.join(DELIMITER, tokens));
        }
    }

    private void processTrainee(String[] tokens) {
        Long userId = Long.parseLong(tokens[1]);

        Trainee trainee = Trainee.builder()
                .userId(userId)
                .firstName(tokens[2])
                .lastName(tokens[3])
                .dateOfBirth(LocalDate.parse(tokens[4]))
                .address(tokens[5])
                .isActive(Boolean.parseBoolean(tokens[6]))
                .build();

        traineeStorage.put(userId, trainee);
    }

    private void processTrainer(String[] tokens) {
        Long userId = Long.parseLong(tokens[1]);

        Trainer trainer = Trainer.builder()
                .userId(userId)
                .firstName(tokens[2])
                .lastName(tokens[3])
                .specialization(new TrainingType(tokens[4]))
                .isActive(Boolean.parseBoolean(tokens[5]))
                .build();

        trainerStorage.put(userId, trainer);
    }

    private void processTraining(String[] tokens) {
        Long trainingId = Long.parseLong(tokens[1]);

        Training training = Training.builder()
                .trainingId(trainingId)
                .traineeId(Long.parseLong(tokens[2]))
                .trainerId(Long.parseLong(tokens[3]))
                .trainingName(tokens[4])
                .trainingType(new TrainingType(tokens[5]))
                .trainingDate(LocalDate.parse(tokens[6]))
                .trainingDuration(Integer.parseInt(tokens[7]))
                .build();

        trainingStorage.put(trainingId, training);
    }
}
