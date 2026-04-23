package com.gym.crm.mapper;

import com.gym.crm.dto.request.CreateTrainerRequest;
import com.gym.crm.dto.request.UpdateTrainerRequest;
import com.gym.crm.dto.response.TrainerResponse;
import com.gym.crm.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {

    public Trainer toEntity(CreateTrainerRequest request) {
        return Trainer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .specialization(request.getSpecialization())
                .isActive(request.isActive())
                .build();
    }

    public Trainer toEntity(UpdateTrainerRequest request) {
        return Trainer.builder()
                .userId(request.getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .specialization(request.getSpecialization())
                .isActive(request.isActive())
                .build();
    }

    public TrainerResponse toResponse(Trainer trainer) {
        return TrainerResponse.builder()
                .id(trainer.getUserId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .username(trainer.getUsername())
                .specialization(trainer.getSpecialization())
                .isActive(trainer.isActive())
                .build();
    }
}