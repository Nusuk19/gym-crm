package com.gym.crm.model;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder(toBuilder = true)
public abstract class User {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private final boolean isActive;
}
