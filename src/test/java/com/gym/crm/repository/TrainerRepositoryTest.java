package com.gym.crm.repository;

import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = {"/datasets/cleanup.sql", "/datasets/trainer-insert.sql"}, executionPhase = BEFORE_TEST_METHOD)
class TrainerRepositoryTest extends AbstractRepositoryTest<TrainerRepository> {

    @Test
    void findByUserUsername_existingTrainer_returnsFullTrainer() {
        Trainer result = repository.findByUserUsername("Mike.Tyson").orElseThrow();

        assertThat(result.getUser().getFirstName()).isEqualTo("Mike");
        assertThat(result.getUser().getLastName()).isEqualTo("Tyson");
        assertThat(result.getUser().getUsername()).isEqualTo("Mike.Tyson");
        assertThat(result.getUser().getIsActive()).isTrue();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getSpecialization()).isNotNull();
        assertThat(result.getSpecialization().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void findByUserUsername_nonExisting_returnsEmpty() {
        Optional<Trainer> result = repository.findByUserUsername("ghost.user");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllWithUserAndSpecialization_returnsAll() {
        List<Trainer> result = repository.findAllWithUserAndSpecialization();

        assertThat(result).hasSizeGreaterThan(0)
                .extracting("user.username")
                .contains("Mike.Tyson", "Adam.Future");
    }

    @Test
    void save_persistsTrainer() {
        Trainer trainer = buildTrainer("Bruce", "Lee", "Bruce.Lee");

        Trainer saved = repository.save(trainer);

        flushAndClear();
        Trainer fromDb = repository.findById(saved.getId()).orElseThrow();
        assertThat(fromDb.getUser().getUsername()).isEqualTo("Bruce.Lee");
        assertThat(fromDb.getUser().getFirstName()).isEqualTo("Bruce");
        assertThat(fromDb.getUser().getLastName()).isEqualTo("Lee");
        assertThat(fromDb.getUser().getIsActive()).isTrue();
        assertThat(fromDb.getSpecialization().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void save_update_changesTrainerData() {
        Trainer trainer = repository.findByUserUsername("Mike.Tyson").orElseThrow();
        Trainer updated = trainer.toBuilder()
                .user(trainer.getUser().toBuilder().firstName("Michael").build())
                .build();

        repository.save(updated);

        flushAndClear();
        Trainer result = repository.findByUserUsername("Mike.Tyson").orElseThrow();
        assertThat(result.getUser().getFirstName()).isEqualTo("Michael");
        assertThat(result.getUser().getUsername()).isEqualTo("Mike.Tyson");
    }

    @Test
    void findAllActiveNotAssignedToTrainee_returnsOnlyUnassigned() {
        List<Trainer> result = repository.findAllActiveNotAssignedToTrainee("Abdul.Hariton");

        assertThat(result).isNotEmpty()
                .extracting("user.username")
                .doesNotContain("Mike.Tyson");
    }

    @Test
    void findAllActiveNotAssignedToTrainee_nonExistingTrainee_returnsAll() {
        List<Trainer> result = repository.findAllActiveNotAssignedToTrainee("ghost.user");

        assertThat(result).extracting("user.username").contains("Mike.Tyson", "Adam.Future");
    }

    @Test
    void existsByUserUsername_existing_returnsTrue() {
        assertThat(repository.existsByUserUsername("Mike.Tyson")).isTrue();
    }

    @Test
    void existsByUserUsername_nonExisting_returnsFalse() {
        assertThat(repository.existsByUserUsername("ghost.user")).isFalse();
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