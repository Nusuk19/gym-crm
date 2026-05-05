package com.gym.crm.dao.impl;

import com.gym.crm.dao.AbstractRepositoryTest;
import com.gym.crm.dao.UserDao;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = "/datasets/user-insert.sql", executionPhase = BEFORE_TEST_METHOD)
class UserDaoImplTest extends AbstractRepositoryTest<UserDao> {

    @Test
    void findByUsername_existingUser_returnsUserWithAllFields() {
        Optional<User> result = dao.findByUsername("Abdul.Hariton");

        assertThat(result).isPresent();

        User user = result.get();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("Abdul");
        assertThat(user.getLastName()).isEqualTo("Hariton");
        assertThat(user.getUsername()).isEqualTo("Abdul.Hariton");
        assertThat(user.getPassword()).isEqualTo("hashedPassword");
        assertThat(user.getIsActive()).isTrue();
    }

    @Test
    void findByUsername_inactiveUser_returnsUserWithCorrectActiveFlag() {
        Optional<User> result = dao.findByUsername("Mike.Tyson");

        assertThat(result).isPresent();
        assertThat(result.get().getIsActive()).isFalse();
        assertThat(result.get().getFirstName()).isEqualTo("Mike");
        assertThat(result.get().getLastName()).isEqualTo("Tyson");
    }

    @Test
    void findByUsername_nonExistingUser_returnsEmpty() {
        Optional<User> result = dao.findByUsername("ghost.user");

        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_nullUsername_returnsEmpty() {
        Optional<User> result = dao.findByUsername("non.existing");

        assertThat(result).isEmpty();
    }

    @Test
    void update_firstName_updatesInDB() {
        User user = dao.findByUsername("Abdul.Hariton").orElseThrow();
        User updated = user.toBuilder()
                .firstName("UpdatedName")
                .build();

        dao.update(updated);

        User result = dao.findByUsername("Abdul.Hariton").orElseThrow();
        assertThat(result.getFirstName()).isEqualTo("UpdatedName");
        assertThat(result.getLastName()).isEqualTo("Hariton");
        assertThat(result.getUsername()).isEqualTo("Abdul.Hariton");
        assertThat(result.getIsActive()).isTrue();
    }

    @Test
    void update_lastName_updatesInDB() {
        User user = dao.findByUsername("Abdul.Hariton").orElseThrow();
        User updated = user.toBuilder()
                .lastName("UpdatedLastName")
                .build();

        dao.update(updated);

        User result = dao.findByUsername("Abdul.Hariton").orElseThrow();
        assertThat(result.getLastName()).isEqualTo("UpdatedLastName");
        assertThat(result.getFirstName()).isEqualTo("Abdul");
    }

    @Test
    void update_password_updatesInDB() {
        User user = dao.findByUsername("Abdul.Hariton").orElseThrow();
        User updated = user.toBuilder()
                .password("newHashedPassword")
                .build();

        dao.update(updated);

        User result = dao.findByUsername("Abdul.Hariton").orElseThrow();
        assertThat(result.getPassword()).isEqualTo("newHashedPassword");
    }

    @Test
    void update_isActive_toFalse_updatesInDB() {
        User user = dao.findByUsername("Abdul.Hariton").orElseThrow();
        assertThat(user.getIsActive()).isTrue();

        User updated = user.toBuilder()
                .isActive(false)
                .build();

        dao.update(updated);

        User result = dao.findByUsername("Abdul.Hariton").orElseThrow();
        assertThat(result.getIsActive()).isFalse();
    }

    @Test
    void update_isActive_toTrue_updatesInDB() {
        User user = dao.findByUsername("Mike.Tyson").orElseThrow();
        assertThat(user.getIsActive()).isFalse();

        User updated = user.toBuilder()
                .isActive(true)
                .build();

        dao.update(updated);

        User result = dao.findByUsername("Mike.Tyson").orElseThrow();
        assertThat(result.getIsActive()).isTrue();
    }

    @Test
    void update_allFields_updatesAllInDB() {
        User user = dao.findByUsername("Abdul.Hariton").orElseThrow();
        User updated = user.toBuilder()
                .firstName("NewFirst")
                .lastName("NewLast")
                .password("newPassword")
                .isActive(false)
                .build();

        User result = dao.update(updated);

        assertThat(result.getFirstName()).isEqualTo("NewFirst");
        assertThat(result.getLastName()).isEqualTo("NewLast");
        assertThat(result.getPassword()).isEqualTo("newPassword");
        assertThat(result.getIsActive()).isFalse();
        assertThat(result.getUsername()).isEqualTo("Abdul.Hariton");

        User fromDb = dao.findByUsername("Abdul.Hariton").orElseThrow();
        assertThat(fromDb.getFirstName()).isEqualTo("NewFirst");
        assertThat(fromDb.getLastName()).isEqualTo("NewLast");
        assertThat(fromDb.getPassword()).isEqualTo("newPassword");
        assertThat(fromDb.getIsActive()).isFalse();
    }

    @Test
    void update_returnsUpdatedEntity() {
        User user = dao.findByUsername("Abdul.Hariton").orElseThrow();
        User updated = user.toBuilder()
                .firstName("ReturnedName")
                .build();

        User result = dao.update(updated);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getFirstName()).isEqualTo("ReturnedName");
        assertThat(result.getLastName()).isEqualTo("Hariton");
        assertThat(result.getUsername()).isEqualTo("Abdul.Hariton");
    }

    @Override
    protected Class<UserDao> getDaoClass() {
        return UserDao.class;
    }
}