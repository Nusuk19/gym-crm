package com.gym.crm.actuator.metrics;

import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymMetricsTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    private MeterRegistry registry;
    private GymMetrics gymMetrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        gymMetrics = new GymMetrics(registry, traineeRepository, trainerRepository);
    }

    @Test
    void incrementTraineeRegistrations_incrementsCounter() {
        gymMetrics.incrementTraineeRegistrations();
        gymMetrics.incrementTraineeRegistrations();

        Counter counter = registry.find("gym_registrations_total").tag("role", "trainee").counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    void incrementTrainerRegistrations_incrementsCounter() {
        gymMetrics.incrementTrainerRegistrations();

        Counter counter = registry.find("gym_registrations_total").tag("role", "trainer").counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void incrementTrainingsCreated_incrementsCounter() {
        gymMetrics.incrementTrainingsCreated();
        gymMetrics.incrementTrainingsCreated();
        gymMetrics.incrementTrainingsCreated();

        Counter counter = registry.find("gym_trainings_created_total").counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(3.0);
    }

    @Test
    void allCounters_startAtZero() {
        Counter traineeCounter = registry.find("gym_registrations_total").tag("role", "trainee").counter();
        Counter trainerCounter = registry.find("gym_registrations_total").tag("role", "trainer").counter();
        Counter trainingsCounter = registry.find("gym_trainings_created_total").counter();

        assertThat(traineeCounter).isNotNull();
        assertThat(trainerCounter).isNotNull();
        assertThat(trainingsCounter).isNotNull();
        assertThat(traineeCounter.count()).isEqualTo(0.0);
        assertThat(trainerCounter.count()).isEqualTo(0.0);
        assertThat(trainingsCounter.count()).isEqualTo(0.0);
    }

    @Test
    void activeTraineesGauge_returnsRepositoryCount() {
        when(traineeRepository.countByUserIsActiveTrue()).thenReturn(5L);

        double actual = registry.get("gym_active_users").tag("role", "trainee").gauge().value();

        assertThat(actual).isEqualTo(5.0);
    }

    @Test
    void activeTrainersGauge_returnsRepositoryCount() {
        when(trainerRepository.countByUserIsActiveTrue()).thenReturn(3L);

        double actual = registry.get("gym_active_users").tag("role", "trainer").gauge().value();

        assertThat(actual).isEqualTo(3.0);
    }

    @Test
    void traineeAndTrainerCounters_areIndependent() {
        gymMetrics.incrementTraineeRegistrations();
        gymMetrics.incrementTraineeRegistrations();
        gymMetrics.incrementTrainerRegistrations();

        assertThat(registry.find("gym_registrations_total").tag("role", "trainee").counter().count()).isEqualTo(2.0);
        assertThat(registry.find("gym_registrations_total").tag("role", "trainer").counter().count()).isEqualTo(1.0);
    }
}