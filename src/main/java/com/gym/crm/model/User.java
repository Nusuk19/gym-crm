package com.gym.crm.model;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public abstract class User {
    private final String firstName;
    private final String lastName;
    private final String userName;
    private final String password;
    private final boolean isActive;
}
