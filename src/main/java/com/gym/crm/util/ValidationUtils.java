package com.gym.crm.util;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void requireValidId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Id must be a positive integer, but was " + id);
        }
    }

    public static void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
