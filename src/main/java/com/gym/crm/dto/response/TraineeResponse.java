package com.gym.crm.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TraineeResponse {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final LocalDate dateOfBirth;
    private final String address;
    private final boolean isActive;
}