package com.gym.crm.repository;

import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = {"/datasets/cleanup.sql", "/datasets/trainer-insert.sql"}, executionPhase = BEFORE_TEST_METHOD)
class TrainerRepositoryTest extends AbstractRepositoryTest<TrainerRepository> {

    @Test
    void findByUserUsername_existingTrainer_returnsFullTrainer() {
        Trainer actual = repository.findByUserUsername("Mike.Tyson").orElseThrow();

        assertNotNull(actual.getId());
        assertEquals("Mike", actual.getUser().getFirstName());
        assertEquals("Tyson", actual.getUser().getLastName());
        assertEquals("Mike.Tyson", actual.getUser().getUsername());
        assertTrue(actual.getUser().getIsActive());
        assertNotNull(actual.getSpecialization());
        assertEquals("Boxing", actual.getSpecialization().getTrainingTypeName());
    }

    @Test
    void findByUserUsername_nonExisting_returnsEmpty() {
        Optional<Trainer> actual = repository.findByUserUsername("ghost.user");

        assertFalse(actual.isPresent());
    }

    @Test
    void findAllWithUserAndSpecialization_returnsAll() {
        List<Trainer> actual = repository.findAllWithUserAndSpecialization();

        List<String> usernames = actual.stream()
                .map(t -> t.getUser().getUsername())
                .toList();

        assertTrue(actual.size() > 0);
        assertTrue(usernames.contains("Mike.Tyson"));
        assertTrue(usernames.contains("Adam.Future"));
    }

    @Test
    void save_newTrainer_persistsAllFields() {
        Trainer trainer = buildTrainer("Bruce", "Lee", "Bruce.Lee");
        Trainer saved = repository.save(trainer);
        Long expectedId = saved.getId();
        flushAndClear();

        Trainer actual = repository.findById(expectedId).orElseThrow();

        assertEquals(expectedId, actual.getId());
        assertEquals("Bruce", actual.getUser().getFirstName());
        assertEquals("Lee", actual.getUser().getLastName());
        assertEquals("Bruce.Lee", actual.getUser().getUsername());
        assertEquals("pass123", actual.getUser().getPassword());
        assertTrue(actual.getUser().getIsActive());
        assertEquals("Boxing", actual.getSpecialization().getTrainingTypeName());
    }

    @Test
    void save_update_existingTrainer_persistsAllFields() {
        Trainer trainer = repository.findByUserUsername("Mike.Tyson").orElseThrow();
        Long expectedId = trainer.getId();

        repository.save(trainer.toBuilder()
                .user(trainer.getUser().toBuilder().firstName("Michael").build())
                .build());
        flushAndClear();

        Trainer actual = repository.findById(expectedId).orElseThrow();

        assertEquals(expectedId, actual.getId());
        assertEquals("Michael", actual.getUser().getFirstName());
        assertEquals("Tyson", actual.getUser().getLastName());
        assertEquals("Mike.Tyson", actual.getUser().getUsername());
        assertTrue(actual.getUser().getIsActive());
        assertEquals("Boxing", actual.getSpecialization().getTrainingTypeName());
    }

    @Test
    void findAllActiveNotAssignedToTrainee_returnsOnlyUnassigned() {
        List<Trainer> actual = repository.findAllActiveNotAssignedToTrainee("Abdul.Hariton");

        List<String> usernames = actual.stream()
                .map(t -> t.getUser().getUsername())
                .toList();

        assertFalse(actual.isEmpty());
        assertFalse(usernames.contains("Mike.Tyson"));
    }

    @Test
    void findAllActiveNotAssignedToTrainee_nonExistingTrainee_returnsAll() {
        List<Trainer> actual = repository.findAllActiveNotAssignedToTrainee("ghost.user");

        List<String> usernames = actual.stream()
                .map(t -> t.getUser().getUsername())
                .toList();

        assertTrue(usernames.contains("Mike.Tyson"));
        assertTrue(usernames.contains("Adam.Future"));
    }

    @Test
    void existsByUserUsername_existing_returnsTrue() {
        boolean actual = repository.existsByUserUsername("Mike.Tyson");

        assertTrue(actual);
    }

    @Test
    void existsByUserUsername_nonExisting_returnsFalse() {
        boolean actual = repository.existsByUserUsername("ghost.user");

        assertFalse(actual);
    }

    private Trainer buildTrainer(String firstName, String lastName, String username) {
        TrainingType boxing = em.getEntityManager()
                .createQuery("FROM TrainingType WHERE trainingTypeName = :name", TrainingType.class)
                .setParameter("name", "Boxing")
                .getSingleResult();

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("pass123")
                .isActive(true)
                .build();

        return Trainer.builder()
                .user(user)
                .specialization(boxing)
                .build();
    }
}