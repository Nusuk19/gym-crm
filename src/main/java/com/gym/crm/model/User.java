package com.gym.crm.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder(toBuilder = true)
public abstract class User {
    private final String firstName;
    private final String lastName;
    private final String username;
    @ToString.Exclude
    private final String password;
    private final boolean isActive;
}
