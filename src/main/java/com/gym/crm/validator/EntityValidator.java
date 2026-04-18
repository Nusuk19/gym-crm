package com.gym.crm.validator;

import com.gym.crm.exception.EntityValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtils {

    public void requireValidId(Long id) {
        if (id == null || id <= 0) {
            throw new EntityValidationException("Id must be a positive integer, but was " + id);
        }
    }

    public void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
