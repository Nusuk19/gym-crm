package com.gym.crm.facade;

import com.gym.crm.dao.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.dao.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.dto.request.ActivationRequest;
import com.gym.crm.dto.request.ChangePasswordRequest;
import com.gym.crm.dto.request.CreateTraineeRequest;
import com.gym.crm.dto.request.CreateTrainerRequest;
import com.gym.crm.dto.request.CreateTrainingRequest;
import com.gym.crm.dto.request.UpdateTraineeRequest;
import com.gym.crm.dto.request.UpdateTrainerRequest;
import com.gym.crm.dto.response.TraineeResponse;
import com.gym.crm.dto.response.TrainerResponse;
import com.gym.crm.dto.response.TrainingResponse;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.common.CoreValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    private TraineeMapper traineeMapper;
    private TrainerMapper trainerMapper;
    private TrainingMapper trainingMapper;
    private CoreValidator coreValidator;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @Autowired
    public void setTraineeMapper(TraineeMapper traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    @Autowired
    public void setTrainerMapper(TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
    }

    @Autowired
    public void setTrainingMapper(TrainingMapper trainingMapper) {
        this.trainingMapper = trainingMapper;
    }

    @Autowired
    public void setValidationService(CoreValidator coreValidator) {
        this.coreValidator = coreValidator;
    }

    public TraineeResponse createTrainee(CreateTraineeRequest request) {
        coreValidator.validate(request);

        return traineeMapper.toResponse(traineeService.create(traineeMapper.toEntity(request)));
    }

    public TraineeResponse updateTrainee(UpdateTraineeRequest request) {
        coreValidator.validate(request);

        return traineeMapper.toResponse(traineeService.update(traineeMapper.toEntity(request)));
    }

    public void deleteTrainee(Long id) {
        traineeService.deleteById(id);
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteByUsername(username);
    }

    public Optional<TraineeResponse> findTraineeById(Long id) {
        return traineeService.findById(id).map(traineeMapper::toResponse);
    }

    public Optional<TraineeResponse> findTraineeByUsername(String username) {
        return traineeService.findByUsername(username).map(traineeMapper::toResponse);
    }

    public List<TraineeResponse> findAllTrainees() {
        return traineeService.findAll().stream()
                .map(traineeMapper::toResponse)
                .toList();
    }

    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        traineeService.updateTrainers(traineeUsername, trainerUsernames);
    }

    public void changeTraineePassword(ChangePasswordRequest request) {
        coreValidator.validate(request);

        traineeService.changePassword(request);
    }

    public void activateTrainee(ActivationRequest request) {
        coreValidator.validate(request);

        traineeService.activate(request);
    }

    public void deactivateTrainee(ActivationRequest request) {
        coreValidator.validate(request);

        traineeService.deactivate(request);
    }

    public TrainerResponse createTrainer(CreateTrainerRequest request) {
        coreValidator.validate(request);

        return trainerMapper.toResponse(trainerService.create(trainerMapper.toEntity(request)));
    }

    public TrainerResponse updateTrainer(UpdateTrainerRequest request) {
        coreValidator.validate(request);

        return trainerMapper.toResponse(trainerService.update(trainerMapper.toEntity(request)));
    }

    public Optional<TrainerResponse> findTrainerById(Long id) {
        return trainerService.findById(id).map(trainerMapper::toResponse);
    }

    public Optional<TrainerResponse> findTrainerByUsername(String username) {
        return trainerService.findByUsername(username).map(trainerMapper::toResponse);
    }

    public List<TrainerResponse> findAllTrainers() {
        return trainerService.findAll().stream()
                .map(trainerMapper::toResponse)
                .toList();
    }

    public List<TrainerResponse> findAllTrainersNotAssignedToTrainee(String traineeUsername) {
        return trainerService.findAllNotAssignedToTrainee(traineeUsername).stream()
                .map(trainerMapper::toResponse)
                .toList();
    }

    public void changeTrainerPassword(ChangePasswordRequest request) {
        coreValidator.validate(request);

        trainerService.changePassword(request);
    }

    public void activateTrainer(ActivationRequest request) {
        coreValidator.validate(request);

        trainerService.activate(request);
    }

    public void deactivateTrainer(ActivationRequest request) {
        coreValidator.validate(request);

        trainerService.deactivate(request);
    }

    public TrainingResponse createTraining(CreateTrainingRequest request) {
        coreValidator.validate(request);

        return trainingMapper.toResponse(trainingService.create(trainingMapper.toEntity(request)));
    }

    public Optional<TrainingResponse> findTrainingById(Long id) {
        return trainingService.findById(id).map(trainingMapper::toResponse);
    }

    public List<TrainingResponse> findAllTrainings() {
        return trainingService.findAll().stream()
                .map(trainingMapper::toResponse)
                .toList();
    }

    public List<TrainingResponse> findTrainingsByTraineeCriteria(TraineeTrainingSearchFilter filter) {
        return trainingService.findByTraineeCriteria(filter).stream()
                .map(trainingMapper::toResponse)
                .toList();
    }

    public List<TrainingResponse> findTrainingsByTrainerCriteria(TrainerTrainingSearchFilter filter) {
        return trainingService.findByTrainerCriteria(filter).stream()
                .map(trainingMapper::toResponse)
                .toList();
    }
}