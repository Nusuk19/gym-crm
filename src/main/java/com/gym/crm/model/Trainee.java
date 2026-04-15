package com.gym.crm.model;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class Trainee extends User {
    private final Long userId;
    private final LocalDate dateOfBirth;
    private final String address;
}
