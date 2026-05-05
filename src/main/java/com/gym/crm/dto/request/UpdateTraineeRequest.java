package com.gym.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UpdateTraineeRequest {

    @NotNull(message = "Id is required")
    @Positive(message = "Id must be a positive number")
    private final Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private final String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private final String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 110, message = "Username must be between 3 and 110 characters")
    private final String username;

    @Past(message = "Date of birth must be in the past")
    private final LocalDate dateOfBirth;

    @Size(max = 100, message = "Address must not exceed 100 characters")
    private final String address;

    private final Boolean isActive;
}
