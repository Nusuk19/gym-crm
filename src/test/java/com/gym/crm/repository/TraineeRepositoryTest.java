package com.gym.crm.repository;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = {"/datasets/cleanup.sql", "/datasets/trainee-insert.sql"}, executionPhase = BEFORE_TEST_METHOD)
class TraineeRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private TraineeRepository traineeRepository;

    @Test
    void findByUserUsername_existingUser_returnsTrainee() {
        Optional<Trainee> result = traineeRepository.findByUserUsername("Abdul.Hariton");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getFirstName()).isEqualTo("Abdul");
        assertThat(result.get().getUser().getLastName()).isEqualTo("Hariton");
        assertThat(result.get().getAddress()).isEqualTo("123 Wolfs St");
        assertThat(result.get().getDateOfBirth()).isEqualTo(LocalDate.of(1994, 5, 6));
    }

    @Test
    void findByUserUsername_nonExistingUser_returnsEmpty() {
        Optional<Trainee> result = traineeRepository.findByUserUsername("ghost.user");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_existingId_returnsTrainee() {
        Long id = traineeRepository.findByUserUsername("Abdul.Hariton").orElseThrow().getId();

        Optional<Trainee> result = traineeRepository.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("Abdul.Hariton");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        assertThat(traineeRepository.findById(999L)).isEmpty();
    }

    @Test
    void findAllWithUser_returnsAllTrainees() {
        List<Trainee> result = traineeRepository.findAllWithUser();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUser().getUsername()).isEqualTo("Abdul.Hariton");
    }

    @Test
    void save_newTrainee_persistsAndReturnsWithId() {
        Trainee newTrainee = buildTrainee("Anna", "Koval", "Anna.Koval");

        Trainee saved = traineeRepository.save(newTrainee);
        em.flush();
        em.clear();

        Optional<Trainee> fromDb = traineeRepository.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getUser().getUsername()).isEqualTo("Anna.Koval");
        assertThat(fromDb.get().getAddress()).isEqualTo("Lviv, Ukraine");
    }

    @Test
    void save_update_existingTrainee_modifiesAddress() {
        Trainee trainee = traineeRepository.findByUserUsername("Abdul.Hariton").orElseThrow();
        Trainee updated = trainee.toBuilder().address("New Address 456").build();

        traineeRepository.save(updated);
        em.flush();
        em.clear();

        Trainee result = traineeRepository.findByUserUsername("Abdul.Hariton").orElseThrow();
        assertThat(result.getAddress()).isEqualTo("New Address 456");
    }

    @Test
    void deleteById_existingId_removesTrainee() {
        Long id = traineeRepository.findByUserUsername("Abdul.Hariton").orElseThrow().getId();

        traineeRepository.deleteById(id);
        em.flush();

        assertThat(traineeRepository.findById(id)).isEmpty();
        assertThat(traineeRepository.findAll()).isEmpty();
    }

    @Test
    void deleteById_nonExistingId_doesNotThrow() {
        assertThatCode(() -> traineeRepository.deleteById(999L)).doesNotThrowAnyException();
    }

    @Test
    void findByUserUsername_traineeHasAssignedTrainer() {
        Trainee trainee = traineeRepository.findByUserUsername("Abdul.Hariton").orElseThrow();

        assertThat(trainee.getTrainers()).hasSize(1);
        assertThat(trainee.getTrainers().getFirst().getUser().getUsername()).isEqualTo("Mike.Tyson");
    }

    @Test
    void existsByUserUsername_existingUser_returnsTrue() {
        assertThat(traineeRepository.existsByUserUsername("Abdul.Hariton")).isTrue();
    }

    @Test
    void existsByUserUsername_nonExistingUser_returnsFalse() {
        assertThat(traineeRepository.existsByUserUsername("ghost.user")).isFalse();
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