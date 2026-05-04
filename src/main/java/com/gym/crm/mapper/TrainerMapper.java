package com.gym.crm.mapper;

import com.gym.crm.dto.request.CreateTrainerRequest;
import com.gym.crm.dto.request.UpdateTrainerRequest;
import com.gym.crm.dto.response.TrainerResponse;
import com.gym.crm.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    @Mapping(target = "specialization", source = "specialization")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    Trainer toEntity(CreateTrainerRequest request);

    @Mapping(target = "user.username", source = "username")
    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    @Mapping(target = "specialization", source = "specialization")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    Trainer toEntity(UpdateTrainerRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "specialization", source = "specialization")
    TrainerResponse toResponse(Trainer trainer);
}