package com.gym.crm.service.common;

import com.gym.crm.dto.request.UserCredentials;
import com.gym.crm.exception.AuthenticationFailedException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.User;
import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.UserRepository;
import com.gym.crm.service.profile.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationService {

    private static final String INVALID_CREDENTIALS = "Invalid credentials";

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void validateTraineeCredentials(UserCredentials credentials) {
        log.info("Validating trainee credentials: username={}", credentials.getUsername());

        Trainee trainee = traineeRepository.findByUserUsername(credentials.getUsername())
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(credentials.getPassword(), trainee.getUser().getPassword())) {
            log.warn("Authentication failed for trainee: username={}", credentials.getUsername());

            throw new AuthenticationFailedException(INVALID_CREDENTIALS);
        }

        log.info("Trainee credentials validated successfully: username={}", credentials.getUsername());
    }

    public void validateTrainerCredentials(UserCredentials credentials) {
        log.info("Validating trainer credentials: username={}", credentials.getUsername());

        Trainer trainer = trainerRepository.findByUserUsername(credentials.getUsername())
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(credentials.getPassword(), trainer.getUser().getPassword())) {
            log.warn("Authentication failed for trainer: username={}", credentials.getUsername());

            throw new AuthenticationFailedException(INVALID_CREDENTIALS);
        }

        log.info("Trainer credentials validated successfully: username={}", credentials.getUsername());
    }

    public void validateCredentials(UserCredentials credentials) {
        log.info("Validating credentials: username={}", credentials.getUsername());

        User user = userRepository.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_CREDENTIALS));


        if (!passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            log.warn("Authentication failed: username={}", credentials.getUsername());

            throw new AuthenticationFailedException(INVALID_CREDENTIALS);
        }

        log.info("Credentials validated successfully: username={}", credentials.getUsername());
    }
}