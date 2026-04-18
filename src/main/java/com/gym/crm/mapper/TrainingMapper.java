package com.gym.crm.mapper;

import com.gym.crm.dto.request.CreateTrainingRequest;
import com.gym.crm.dto.response.TrainingResponse;
import com.gym.crm.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public Training toEntity(CreateTrainingRequest request) {
        return Training.builder()
                .traineeId(request.getTraineeId())
                .trainerId(request.getTrainerId())
                .trainingName(request.getTrainingName())
                .trainingType(request.getTrainingType())
                .trainingDate(request.getTrainingDate())
                .trainingDuration(request.getTrainingDuration())
                .build();
    }

    public TrainingResponse toResponse(Training training) {
        return TrainingResponse.builder()
                .id(training.getTrainingId())
                .traineeId(training.getTraineeId())
                .trainerId(training.getTrainerId())
                .trainingName(training.getTrainingName())
                .trainingType(training.getTrainingType())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }
}