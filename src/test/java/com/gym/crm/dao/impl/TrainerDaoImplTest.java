package com.gym.crm.dao.impl;

import com.gym.crm.dao.AbstractRepositoryTest;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = {"/datasets/cleanup.sql", "/datasets/trainer-insert.sql"}, executionPhase = BEFORE_TEST_METHOD)
class TrainerDaoImplTest extends AbstractRepositoryTest<TrainerDao> {

    @Test
    void findByUsername_existingTrainer_returnsFullTrainer() {
        Trainer result = dao.findByUsername("Mike.Tyson").orElseThrow();

        assertThat(result.getUser().getFirstName()).isEqualTo("Mike");
        assertThat(result.getUser().getLastName()).isEqualTo("Tyson");
        assertThat(result.getUser().getUsername()).isEqualTo("Mike.Tyson");
        assertThat(result.getUser().getPassword()).isEqualTo("hashedPassword32");
        assertThat(result.getUser().isActive()).isTrue();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getSpecialization()).isNotNull();
        assertThat(result.getSpecialization().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void findByUsername_nonExisting_returnsEmpty() {
        Optional<Trainer> result = dao.findByUsername("ghost.user");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_existing_returnsTrainer_withAllFields() {
        Trainer trainer = dao.findByUsername("Mike.Tyson").orElseThrow();

        Trainer result = dao.findById(trainer.getId()).orElseThrow();

        assertThat(result.getUser().getUsername()).isEqualTo("Mike.Tyson");
        assertThat(result.getUser().getFirstName()).isEqualTo("Mike");
        assertThat(result.getSpecialization().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void findAll_returnsAllTrainers() {
        List<Trainer> result = dao.findAll();

        assertThat(result).hasSizeGreaterThan(0)
                .extracting("user.username")
                .contains("Mike.Tyson", "Adam.Future");
    }

    @Test
    void save_persistsTrainer_completely() {
        Trainer trainer = buildTrainer("Bruce", "Lee", "Bruce.Lee");

        Trainer saved = dao.save(trainer);

        Trainer fromDb = dao.findById(saved.getId()).orElseThrow();

        assertThat(fromDb.getUser().getUsername()).isEqualTo("Bruce.Lee");
        assertThat(fromDb.getUser().getFirstName()).isEqualTo("Bruce");
        assertThat(fromDb.getUser().getLastName()).isEqualTo("Lee");
        assertThat(fromDb.getUser().isActive()).isTrue();
        assertThat(fromDb.getSpecialization().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void update_changesTrainerData() {
        Trainer trainer = dao.findByUsername("Mike.Tyson").orElseThrow();
        Trainer updated = trainer.toBuilder()
                .user(trainer.getUser().toBuilder()
                        .firstName("Michael")
                        .build())
                .build();

        dao.update(updated);

        Trainer result = dao.findByUsername("Mike.Tyson").orElseThrow();

        assertThat(result.getUser().getFirstName()).isEqualTo("Michael");
        assertThat(result.getUser().getUsername()).isEqualTo("Mike.Tyson");
        assertThat(result.getSpecialization().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void findAllNotAssignedToTrainee_returnsOnlyUnassigned() {
        List<Trainer> result = dao.findAllNotAssignedToTrainee("Abdul.Hariton");

        assertThat(result).isNotEmpty()
                .extracting("user.username")
                .doesNotContain("Mike.Tyson");
    }

    @Test
    void findAllNotAssignedToTrainee_nonExisting_returnsAll() {
        List<Trainer> result = dao.findAllNotAssignedToTrainee("ghost.user");

        assertThat(result).extracting("user.username").contains("Mike.Tyson", "Adam.Future");
    }

    @Test
    void save_shouldPersistUserAndSpecialization() {
        Trainer trainer = buildTrainer("Bruce", "Lee", "Bruce.Lee");

        dao.save(trainer);

        Trainer fromDb = dao.findByUsername("Bruce.Lee").orElseThrow();

        assertThat(fromDb.getUser()).isNotNull();
        assertThat(fromDb.getSpecialization()).isNotNull();
    }

    private Trainer buildTrainer(String firstName, String lastName, String username) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("pass123")
                .isActive(true)
                .build();

        TrainingType boxing = sessionFactory
                .openSession()
                .createQuery("FROM TrainingType WHERE trainingTypeName = :name", TrainingType.class)
                .setParameter("name", "Boxing")
                .uniqueResult();

        return Trainer.builder()
                .user(user)
                .specialization(boxing)
                .build();
    }

    @Override
    protected Class<TrainerDao> getDaoClass() {
        return TrainerDao.class;
    }
}