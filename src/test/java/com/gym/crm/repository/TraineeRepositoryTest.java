package com.gym.crm.repository;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = {"/datasets/cleanup.sql", "/datasets/trainee-insert.sql"}, executionPhase = BEFORE_TEST_METHOD)
class TraineeRepositoryTest extends AbstractRepositoryTest<TraineeRepository> {

    @Test
    void findByUserUsername_existingUser_returnsTrainee() {
        Optional<Trainee> actual = repository.findByUserUsername("Abdul.Hariton");

        assertTrue(actual.isPresent());
        assertEquals("Abdul", actual.get().getUser().getFirstName());
        assertEquals("Hariton", actual.get().getUser().getLastName());
        assertEquals("123 Wolfs St", actual.get().getAddress());
        assertEquals(LocalDate.of(1994, 5, 6), actual.get().getDateOfBirth());
    }

    @Test
    void findByUserUsername_nonExistingUser_returnsEmpty() {
        Optional<Trainee> actual = repository.findByUserUsername("ghost.user");

        assertFalse(actual.isPresent());
    }

    @Test
    void findAllWithUser_returnsAllTrainees() {
        List<Trainee> actual = repository.findAllWithUser();

        assertEquals(1, actual.size());
        assertEquals("Abdul.Hariton", actual.iterator().next().getUser().getUsername());
    }

    @Test
    void save_newTrainee_persistsAllFields() {
        Trainee newTrainee = buildTrainee("Anna", "Koval", "Anna.Koval");
        Trainee saved = repository.save(newTrainee);
        Long expectedId = saved.getId();
        flushAndClear();

        Trainee actual = repository.findById(expectedId).orElseThrow();

        assertEquals(expectedId, actual.getId());
        assertEquals("Anna", actual.getUser().getFirstName());
        assertEquals("Koval", actual.getUser().getLastName());
        assertEquals("Anna.Koval", actual.getUser().getUsername());
        assertEquals("pass123", actual.getUser().getPassword());
        assertTrue(actual.getUser().getIsActive());
        assertEquals("Lviv, Ukraine", actual.getAddress());
        assertEquals(LocalDate.of(1997, 3, 22), actual.getDateOfBirth());
    }

    @Test
    void save_update_existingTrainee_persistsAllFields() {
        Trainee trainee = repository.findByUserUsername("Abdul.Hariton").orElseThrow();
        Long expectedId = trainee.getId();

        repository.save(trainee.toBuilder().address("New Address 456").build());
        flushAndClear();

        Trainee actual = repository.findById(expectedId).orElseThrow();

        assertEquals(expectedId, actual.getId());
        assertEquals("Abdul", actual.getUser().getFirstName());
        assertEquals("Hariton", actual.getUser().getLastName());
        assertEquals("Abdul.Hariton", actual.getUser().getUsername());
        assertTrue(actual.getUser().getIsActive());
        assertEquals("New Address 456", actual.getAddress());
        assertEquals(LocalDate.of(1994, 5, 6), actual.getDateOfBirth());
    }

    @Test
    void deleteById_existingId_removesTrainee() {
        Long id = repository.findByUserUsername("Abdul.Hariton").orElseThrow().getId();

        repository.deleteById(id);
        flushAndClear();

        Optional<Trainee> deleted = repository.findById(id);
        List<Trainee> all = repository.findAll();

        assertFalse(deleted.isPresent());
        assertEquals(0, all.size());
    }

    @Test
    void findByUserUsername_traineeHasAssignedTrainer() {
        Trainee trainee = repository.findByUserUsername("Abdul.Hariton").orElseThrow();

        assertEquals(1, trainee.getTrainers().size());
        assertEquals("Mike.Tyson", trainee.getTrainers().iterator().next().getUser().getUsername());
    }

    @Test
    void existsByUserUsername_existingUser_returnsTrue() {
        boolean result = repository.existsByUserUsername("Abdul.Hariton");

        assertTrue(result);
    }

    @Test
    void existsByUserUsername_nonExistingUser_returnsFalse() {
        boolean result = repository.existsByUserUsername("ghost.user");

        assertFalse(result);
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