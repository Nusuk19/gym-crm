package com.gym.crm.dto.request;

import com.gym.crm.model.TrainingType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateTrainerRequest {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final TrainingType specialization;
    private final boolean isActive;
    private final String username;
}