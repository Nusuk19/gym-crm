package com.gym.crm.dto.response;

import com.gym.crm.model.TrainingType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainerResponse {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final TrainingType specialization;
    private final Boolean isActive;
}