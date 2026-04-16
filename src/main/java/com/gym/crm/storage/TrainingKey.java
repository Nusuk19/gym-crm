package com.gym.crm.storage;

import java.time.LocalDate;

public record TrainingKey(
        Long traineeId,
        Long trainerId,
        LocalDate trainingDate
) {
}