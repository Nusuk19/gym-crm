package com.gym.crm.dao.impl;

import com.gym.crm.dao.AbstractRepositoryTest;
import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.User;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = {"/datasets/cleanup.sql", "/datasets/trainee-insert.sql"}, executionPhase = BEFORE_TEST_METHOD)
class TraineeDaoImplTest extends AbstractRepositoryTest<TraineeDao> {

    @Test
    void findByUsername_existingUser_returnsTrainee() {
        Optional<Trainee> result = dao.findByUsername("Abdul.Hariton");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getFirstName()).isEqualTo("Abdul");
        assertThat(result.get().getUser().getLastName()).isEqualTo("Hariton");
        assertThat(result.get().getAddress()).isEqualTo("123 Wolfs St");
        assertThat(result.get().getDateOfBirth()).isEqualTo(LocalDate.of(1994, 5, 6));
    }

    @Test
    void findByUsername_nonExistingUser_returnsEmpty() {
        Optional<Trainee> result = dao.findByUsername("ghost.user");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_existingId_returnsTrainee() {
        Long existingId = dao.findByUsername("Abdul.Hariton")
                .orElseThrow()
                .getId();

        Optional<Trainee> result = dao.findById(existingId);

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("Abdul.Hariton");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Trainee> result = dao.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsAllTrainees() {
        List<Trainee> result = dao.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUser().getUsername()).isEqualTo("Abdul.Hariton");
    }

    @Test
    void save_newTrainee_persistsAndReturnsWithId() {
        Trainee newTrainee = buildTrainee("Anna", "Koval", "Anna.Koval");

        Trainee saved = dao.save(newTrainee);

        Optional<Trainee> fromDb = dao.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getUser().getUsername()).isEqualTo("Anna.Koval");
        assertThat(fromDb.get().getAddress()).isEqualTo("Lviv, Ukraine");
    }

    @Test
    void update_existingTrainee_modifiesAddress() {
        Trainee trainee = dao.findByUsername("Abdul.Hariton").orElseThrow();
        Trainee updated = trainee.toBuilder()
                .address("New Address 456")
                .build();

        dao.update(updated);

        Trainee result = dao.findByUsername("Abdul.Hariton").orElseThrow();
        assertThat(result.getAddress()).isEqualTo("New Address 456");
    }

    @Test
    void deleteByUsername_existingUser_removesTrainee() {
        dao.deleteByUsername("Abdul.Hariton");

        assertThat(dao.findByUsername("Abdul.Hariton")).isEmpty();
        assertThat(dao.findAll()).isEmpty();
    }

    @Test
    void deleteByUsername_nonExistingUser_doesNotThrow() {
        dao.deleteByUsername("nobody.here");

        assertThat(dao.findAll()).hasSize(1);
    }

    @Test
    void deleteByUsername_cascadesTrainings() {
        long trainingsBefore = countTrainings();
        assertThat(trainingsBefore).isEqualTo(1);

        dao.deleteByUsername("Abdul.Hariton");

        assertThat(dao.findByUsername("Abdul.Hariton")).isEmpty();
        assertThat(countTrainings()).isZero();
    }

    @Test
    void findByUsername_traineeHasAssignedTrainer() {
        Trainee trainee = dao.findByUsername("Abdul.Hariton").orElseThrow();

        assertThat(trainee.getTrainers()).hasSize(1);
        assertThat(trainee.getTrainers().getFirst().getUser().getUsername()).isEqualTo("Mike.Tyson");
    }

    @Test
    void save_shouldAlsoPersistUser() {
        Trainee trainee = buildTrainee("Adam", "Bextra", "Adam.Bextra");

        dao.save(trainee);

        try (Session session = sessionFactory.openSession()) {
            Long count = session
                    .createQuery("SELECT COUNT(u) FROM User u WHERE username = :u", Long.class)
                    .setParameter("u", "Adam.Bextra")
                    .getSingleResult();

            assertThat(count).isEqualTo(1);
        }
    }

    @Test
    void save_persistsToDatabase_countIncreases() {
        long before = countTrainees();

        dao.save(buildTrainee("Test", "User", "Test.User"));

        assertThat(countTrainees()).isEqualTo(before + 1);
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

    private long countTrainees() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT COUNT(t) FROM Trainee t", Long.class)
                    .getSingleResult();
        }
    }

    private long countTrainings() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT COUNT(t) FROM Training t", Long.class)
                    .getSingleResult();
        }
    }

    @Override
    protected Class<TraineeDao> getDaoClass() {
        return TraineeDao.class;
    }
}