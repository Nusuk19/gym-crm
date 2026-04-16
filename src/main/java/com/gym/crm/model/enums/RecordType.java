package com.gym.crm.model.enums;

public enum RecordType {
    TRAINEE,
    TRAINER,
    TRAINING;

    public static RecordType from(String value) {
        try {
            return RecordType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Unknown record type: " + value);
        }
    }
}