package com.gym.crm.profile;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UsernameGenerator {
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public String generate(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        Set<String> existingUsernames = getAllExistingUsernames();

        if (!existingUsernames.contains(baseUsername)) {
            return baseUsername;
        }

        int serialNumber = 1;
        while (existingUsernames.contains(baseUsername + serialNumber)) {
            serialNumber++;
        }

        return baseUsername + serialNumber;
    }

    private Set<String> getAllExistingUsernames() {
        return Stream.concat(
                        traineeDao.findAll().stream().map(Trainee::getUsername),
                        trainerDao.findAll().stream().map(Trainer::getUsername)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
