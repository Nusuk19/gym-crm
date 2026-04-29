package com.gym.crm.dao.impl;

import com.gym.crm.dao.AbstractRepositoryTest;
import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = "/datasets/trainee-insert.sql", executionPhase = BEFORE_TEST_METHOD)
class TraineeDaoImplTest extends AbstractRepositoryTest {

    @Autowired
    private TraineeDao traineeDao;

    @Test
    void findByUsername_existingUser_returnsTrainee() {
        Optional<Trainee> result = traineeDao.findByUsername("Abdul.Hariton");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getFirstName()).isEqualTo("Abdul");
        assertThat(result.get().getUser().getLastName()).isEqualTo("Hariton");
        assertThat(result.get().getAddress()).isEqualTo("123 Wolfs St");
        assertThat(result.get().getDateOfBirth()).isEqualTo(LocalDate.of(1994, 5, 6));
    }

    @Test
    void findByUsername_nonExistingUser_returnsEmpty() {
        Optional<Trainee> result = traineeDao.findByUsername("ghost.user");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_existingId_returnsTrainee() {
        Long existingId = traineeDao.findByUsername("Abdul.Hariton")
                .orElseThrow()
                .getId();

        Optional<Trainee> result = traineeDao.findById(existingId);

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("Abdul.Hariton");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Trainee> result = traineeDao.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsAllTrainees() {
        List<Trainee> result = traineeDao.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUser().getUsername()).isEqualTo("Abdul.Hariton");
    }

    @Test
    void save_newTrainee_persistsAndReturnsWithId() {
        Trainee newTrainee = buildTrainee("Anna", "Koval", "Anna.Koval");

        Trainee saved = traineeDao.save(newTrainee);
        flushAndClear();

        Optional<Trainee> fromDb = traineeDao.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getUser().getUsername()).isEqualTo("Anna.Koval");
        assertThat(fromDb.get().getAddress()).isEqualTo("Lviv, Ukraine");
    }

    @Test
    void update_existingTrainee_modifiesAddress() {
        Trainee trainee = traineeDao.findByUsername("Abdul.Hariton").orElseThrow();
        Trainee updated = trainee.toBuilder()
                .address("New Address 456")
                .build();

        traineeDao.update(updated);
        flushAndClear();

        Trainee result = traineeDao.findByUsername("Abdul.Hariton").orElseThrow();
        assertThat(result.getAddress()).isEqualTo("New Address 456");
    }

    @Test
    void deleteByUsername_existingUser_removesTrainee() {
        traineeDao.deleteByUsername("Abdul.Hariton");
        flushAndClear();

        assertThat(traineeDao.findByUsername("Abdul.Hariton")).isEmpty();
        assertThat(traineeDao.findAll()).isEmpty();
    }

    @Test
    void deleteByUsername_nonExistingUser_doesNotThrow() {
        traineeDao.deleteByUsername("nobody.here");
        flushAndClear();

        assertThat(traineeDao.findAll()).hasSize(1);
    }

    @Test
    void deleteByUsername_cascadesTrainings() {
        long trainingsBefore = countTrainings();
        assertThat(trainingsBefore).isEqualTo(1);

        traineeDao.deleteByUsername("Abdul.Hariton");
        flushAndClear();

        assertThat(traineeDao.findByUsername("Abdul.Hariton")).isEmpty();
        assertThat(countTrainings()).isZero();
    }

    @Test
    void findByUsername_traineeHasAssignedTrainer() {
        Trainee trainee = traineeDao.findByUsername("Abdul.Hariton").orElseThrow();

        assertThat(trainee.getTrainers()).hasSize(1);
        assertThat(trainee.getTrainers().getFirst().getUser().getUsername())
                .isEqualTo("Mike.Tyson");
    }

    @Test
    void save_shouldAlsoPersistUser() {
        Trainee trainee = buildTrainee("Adam", "Bextra", "Adam.Bextra");

        traineeDao.save(trainee);
        flushAndClear();

        Long count = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(u) FROM User u WHERE username = :u", Long.class)
                .setParameter("u", "Adam.Bextra")
                .getSingleResult();

        assertThat(count).isEqualTo(1);
    }

    @Test
    void save_persistsToDatabase_countIncreases() {
        long before = countTrainees();

        traineeDao.save(buildTrainee("Test", "User", "Test.User"));
        flushAndClear();

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
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(t) FROM Trainee t", Long.class)
                .getSingleResult();
    }

    private long countTrainings() {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(t) FROM Training t", Long.class)
                .getSingleResult();
    }
}