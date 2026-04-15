package com.gym.crm.model;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.ui.Model;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class Trainer extends User{
    private final Long userId;
    private final TrainingType specialization;
}
