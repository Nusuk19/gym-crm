package com.gym.crm.mapper;

import com.gym.crm.dto.request.CreateTraineeRequest;
import com.gym.crm.dto.request.UpdateTraineeRequest;
import com.gym.crm.dto.response.TraineeResponse;
import com.gym.crm.model.Trainee;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper {

    public Trainee toEntity(CreateTraineeRequest request) {
        return Trainee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .isActive(request.isActive())
                .build();
    }

    public Trainee toEntity(UpdateTraineeRequest request) {
        return Trainee.builder()
                .userId(request.getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .isActive(request.isActive())
                .build();
    }

    public TraineeResponse toResponse(Trainee trainee) {
        return TraineeResponse.builder()
                .id(trainee.getUserId())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .username(trainee.getUsername())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.isActive())
                .build();
    }
}