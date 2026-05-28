package com.gym.crm.repository;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = {"/datasets/cleanup.sql", "/datasets/trainee-insert.sql"}, executionPhase = BEFORE_TEST_METHOD)
class TraineeRepositoryTest extends AbstractRepositoryTest<TraineeRepository> {

    @Test
    void findByUserUsername_existingUser_returnsTrainee() {
        Optional<Trainee> result = repository.findByUserUsername("Abdul.Hariton");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getFirstName()).isEqualTo("Abdul");
        assertThat(result.get().getUser().getLastName()).isEqualTo("Hariton");
        assertThat(result.get().getAddress()).isEqualTo("123 Wolfs St");
        assertThat(result.get().getDateOfBirth()).isEqualTo(LocalDate.of(1994, 5, 6));
    }

    @Test
    void findByUserUsername_nonExistingUser_returnsEmpty() {
        Optional<Trainee> result = repository.findByUserUsername("ghost.user");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllWithUser_returnsAllTrainees() {
        List<Trainee> result = repository.findAllWithUser();

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getUser().getUsername()).isEqualTo("Abdul.Hariton");
    }

    @Test
    void save_newTrainee_persistsAndReturnsWithId() {
        Trainee newTrainee = buildTrainee("Anna", "Koval", "Anna.Koval");
        Trainee saved = repository.save(newTrainee);

        flushAndClear();
        Long savedId = saved.getId();
        Optional<Trainee> fromDb = repository.findById(savedId);
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getUser().getUsername()).isEqualTo("Anna.Koval");
        assertThat(fromDb.get().getAddress()).isEqualTo("Lviv, Ukraine");
    }

    @Test
    void save_update_existingTrainee_modifiesAddress() {
        Trainee trainee = repository.findByUserUsername("Abdul.Hariton").orElseThrow();
        Trainee updated = trainee.toBuilder().address("New Address 456").build();

        repository.save(updated);

        flushAndClear();
        Trainee result = repository.findByUserUsername("Abdul.Hariton").orElseThrow();
        assertThat(result.getAddress()).isEqualTo("New Address 456");
    }

    @Test
    void deleteById_existingId_removesTrainee() {
        Long id = repository.findByUserUsername("Abdul.Hariton").orElseThrow().getId();

        repository.deleteById(id);

        flushAndClear();
        Optional<Trainee> deleted = repository.findById(id);
        List<Trainee> all = repository.findAll();
        assertThat(deleted).isEmpty();
        assertThat(all).isEmpty();
    }

    @Test
    void findByUserUsername_traineeHasAssignedTrainer() {
        Trainee trainee = repository.findByUserUsername("Abdul.Hariton").orElseThrow();

        assertThat(trainee.getTrainers()).hasSize(1);
        assertThat(trainee.getTrainers().iterator().next().getUser().getUsername()).isEqualTo("Mike.Tyson");
    }

    @Test
    void existsByUserUsername_existingUser_returnsTrue() {
        boolean exists = repository.existsByUserUsername("Abdul.Hariton");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUserUsername_nonExistingUser_returnsFalse() {
        boolean exists = repository.existsByUserUsername("ghost.user");

        assertThat(exists).isFalse();
    }

    private Trainee buildTrainee(String firstName, String lastName, String username) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("pass123")
                .isActive(true)
                .build();

        return Trainee.builder()
                .user(user)
                .address("Lviv, Ukraine")
                .dateOfBirth(LocalDate.of(1997, 3, 22))
                .build();
    }
}