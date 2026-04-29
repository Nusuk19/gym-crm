package com.gym.crm.init;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final TraineeDao traineeDao;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("=== INIT START ===");

        // CREATE
        traineeDao.save(build("Jihn", "Smith", "Jihn.Smith"));
        traineeDao.save(build("Jonhn", "Tyson", "Jonhn.Tyson"));
        Trainee anna = traineeDao.save(build("Ben", "Dextra", "Ben.Dextra"));

        log.info("Created 3 trainees");

        // UPDATE
        Trainee persistedAnna = traineeDao.findByUsername("Ben.Dextra")
                .orElseThrow();

        Trainee updatedAnna = persistedAnna.toBuilder()
                .address("Updated Address 123")
                .build();

        traineeDao.update(updatedAnna);

        log.info("Updated trainee: {}", updatedAnna.getUser().getUsername());

        // DELETE
        traineeDao.deleteByUsername("Jonhn.Tyson");

        log.info("Deleted trainee: Jonhn.Tyson");

        log.info("=== INIT END ===");
    }

    private Trainee build(String firstName, String lastName, String username) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("pass123")
                .isActive(true)
                .build();

        return Trainee.builder()
                .user(user)
                .address("Default address")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .build();
    }
}