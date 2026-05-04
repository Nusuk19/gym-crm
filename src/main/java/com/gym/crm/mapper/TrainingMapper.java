package com.gym.crm.mapper;

import com.gym.crm.dto.request.CreateTrainingRequest;
import com.gym.crm.dto.response.TrainingResponse;
import com.gym.crm.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingMapper {

    @Mapping(target = "trainee.id", source = "traineeId")
    @Mapping(target = "trainer.id", source = "trainerId")
    @Mapping(target = "name", source = "trainingName")
    @Mapping(target = "trainingType.trainingTypeName", source = "trainingType.trainingTypeName")
    Training toEntity(CreateTrainingRequest request);

    @Mapping(target = "traineeId", source = "trainee.id")
    @Mapping(target = "trainerId", source = "trainer.id")
    @Mapping(target = "trainingName", source = "name")
    @Mapping(target = "trainingType", source = "trainingType")
    TrainingResponse toResponse(Training training);
}