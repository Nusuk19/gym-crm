package com.gym.crm.model;

import java.time.LocalDate;

public record TrainingKey(
        Long traineeId,
        Long trainerId,
        LocalDate trainingDate
) {
}