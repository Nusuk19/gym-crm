package com.gym.crm.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CreateTraineeRequest {
    private final String firstName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    private final String address;
    private final boolean isActive;
}
