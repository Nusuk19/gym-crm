package com.gym.crm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymCrmApplicationTest {

    @Test
    void main_ShouldLoadSpringContextSuccessfully() {
        try (MockedConstruction<AnnotationConfigApplicationContext> mockedContext =
                     mockConstruction(AnnotationConfigApplicationContext.class,
                             (mock, context) -> {
                             })) {

            assertDoesNotThrow(() -> GymCrmApplication.main(new String[]{}));

            assertThat(mockedContext.constructed()).hasSize(1);
        }
    }

    @Test
    void main_ShouldCloseContextAfterUse() {
        try (MockedConstruction<AnnotationConfigApplicationContext> mockedContext =
                     mockConstruction(AnnotationConfigApplicationContext.class)) {

            GymCrmApplication.main(new String[]{});

            AnnotationConfigApplicationContext context = mockedContext.constructed().get(0);
            verify(context, times(1)).close();
        }
    }

    @Test
    void main_ShouldNotThrowWhenCalledWithNullArgs() {
        try (MockedConstruction<AnnotationConfigApplicationContext> ignored =
                     mockConstruction(AnnotationConfigApplicationContext.class)) {

            assertDoesNotThrow(() -> GymCrmApplication.main(null));
        }
    }

    @Test
    void main_ShouldNotThrowWhenCalledWithEmptyArgs() {
        try (MockedConstruction<AnnotationConfigApplicationContext> ignored =
                     mockConstruction(AnnotationConfigApplicationContext.class)) {

            assertDoesNotThrow(() -> GymCrmApplication.main(new String[]{}));
        }
    }

    @Test
    void main_ShouldCloseContextEvenIfExceptionOccurs() {
        try (MockedConstruction<AnnotationConfigApplicationContext> mockedContext =
                     mockConstruction(AnnotationConfigApplicationContext.class,
                             (mock, context) -> doThrow(new RuntimeException("Context error"))
                                     .when(mock)
                                     .close())) {

            assertThrows(RuntimeException.class, () -> GymCrmApplication.main(new String[]{}));

            AnnotationConfigApplicationContext context = mockedContext.constructed().get(0);
            verify(context, times(1)).close();
        }
    }
}