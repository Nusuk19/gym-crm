package com.gym.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateTrainerRequest {

    @NotNull(message = "Id cannot be null")
    @Positive(message = "Id must be a positive number")
    private final Long id;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private final String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private final String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 110, message = "Username must be between 3 and 110 characters")
    private final String username;

    @NotNull(message = "Specialization is required")
    @Positive(message = "Specialization id must be a positive number")
    private final Long specializationId;

    private final Boolean isActive;
}