package com.gym.crm.dto.request;

import com.gym.crm.model.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CreateTrainingRequest {

    @NotNull(message = "Trainee id is required")
    @Positive(message = "Trainee id must be a positive number")
    private final Long traineeId;

    @NotNull(message = "Trainer id is required")
    @Positive(message = "Trainer id must be a positive number")
    private final Long trainerId;

    @NotBlank(message = "Training name is required")
    @Size(max = 100, message = "Training name must not exceed 100 characters")
    private final String trainingName;

    @NotNull(message = "Training type is required")
    private final TrainingType trainingType;

    @NotNull(message = "Training date is required")
    private final LocalDate trainingDate;

    @Positive(message = "Training duration must be a positive number")
    private final int trainingDuration;
}