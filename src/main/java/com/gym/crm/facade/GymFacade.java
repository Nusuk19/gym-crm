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
import com.gym.crm.dto.request.UserCredentials;
import com.gym.crm.dto.response.TraineeResponse;
import com.gym.crm.dto.response.TrainerResponse;
import com.gym.crm.dto.response.TrainingResponse;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.common.AuthenticationService;
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
    private AuthenticationService authenticationService;

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

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public TraineeResponse createTrainee(CreateTraineeRequest request) {
        coreValidator.validate(request);

        return traineeMapper.toResponse(traineeService.create(traineeMapper.toEntity(request)));
    }

    public TraineeResponse updateTrainee(UpdateTraineeRequest request, UserCredentials credentials) {
        coreValidator.validate(request);
        coreValidator.validate(credentials);

        authenticationService.validateTraineeCredentials(credentials);

        return traineeMapper.toResponse(traineeService.update(traineeMapper.toEntity(request)));
    }

    public void deleteTrainee(Long id, UserCredentials credentials) {
        coreValidator.validate(id);
        coreValidator.validate(credentials);

        authenticationService.validateTraineeCredentials(credentials);

        traineeService.deleteById(id);
    }

    public void deleteTraineeByUsername(String username, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTraineeCredentials(credentials);

        traineeService.deleteByUsername(username);
    }

    public Optional<TraineeResponse> findTraineeById(Long id, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTraineeCredentials(credentials);

        return traineeService.findById(id).map(traineeMapper::toResponse);
    }

    public Optional<TraineeResponse> findTraineeByUsername(String username, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTraineeCredentials(credentials);

        return traineeService.findByUsername(username).map(traineeMapper::toResponse);
    }

    public List<TraineeResponse> findAllTrainees(UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTraineeCredentials(credentials);

        return traineeService.findAll().stream()
                .map(traineeMapper::toResponse)
                .toList();
    }

    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTraineeCredentials(credentials);

        traineeService.updateTrainers(traineeUsername, trainerUsernames);
    }

    public void changeTraineePassword(ChangePasswordRequest request, UserCredentials credentials) {
        coreValidator.validate(request);
        coreValidator.validate(credentials);

        authenticationService.validateTraineeCredentials(credentials);

        traineeService.changePassword(request);
    }

    public void activateTrainee(ActivationRequest request, UserCredentials credentials) {
        coreValidator.validate(request);
        coreValidator.validate(credentials);

        authenticationService.validateTraineeCredentials(credentials);

        traineeService.activate(request);
    }

    public void deactivateTrainee(ActivationRequest request, UserCredentials credentials) {
        coreValidator.validate(request);
        coreValidator.validate(credentials);

        authenticationService.validateTraineeCredentials(credentials);

        traineeService.deactivate(request);
    }

    public TrainerResponse createTrainer(CreateTrainerRequest request) {
        coreValidator.validate(request);

        return trainerMapper.toResponse(trainerService.create(trainerMapper.toEntity(request)));
    }

    public TrainerResponse updateTrainer(UpdateTrainerRequest request, UserCredentials credentials) {
        coreValidator.validate(request);
        coreValidator.validate(credentials);

        authenticationService.validateTrainerCredentials(credentials);

        return trainerMapper.toResponse(trainerService.update(trainerMapper.toEntity(request)));
    }

    public Optional<TrainerResponse> findTrainerById(Long id, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTrainerCredentials(credentials);

        return trainerService.findById(id).map(trainerMapper::toResponse);
    }

    public Optional<TrainerResponse> findTrainerByUsername(String username, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTrainerCredentials(credentials);

        return trainerService.findByUsername(username).map(trainerMapper::toResponse);
    }

    public List<TrainerResponse> findAllTrainers(UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTrainerCredentials(credentials);

        return trainerService.findAll().stream()
                .map(trainerMapper::toResponse)
                .toList();
    }

    public List<TrainerResponse> findAllTrainersNotAssignedToTrainee(String traineeUsername, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTraineeCredentials(credentials);

        return trainerService.findAllNotAssignedToTrainee(traineeUsername).stream()
                .map(trainerMapper::toResponse)
                .toList();
    }

    public void changeTrainerPassword(ChangePasswordRequest request, UserCredentials credentials) {
        coreValidator.validate(request);
        coreValidator.validate(credentials);

        authenticationService.validateTrainerCredentials(credentials);

        trainerService.changePassword(request);
    }

    public void activateTrainer(ActivationRequest request, UserCredentials credentials) {
        coreValidator.validate(request);
        coreValidator.validate(credentials);

        authenticationService.validateTrainerCredentials(credentials);

        trainerService.activate(request);
    }

    public void deactivateTrainer(ActivationRequest request, UserCredentials credentials) {
        coreValidator.validate(request);
        coreValidator.validate(credentials);

        authenticationService.validateTrainerCredentials(credentials);

        trainerService.deactivate(request);
    }

    public TrainingResponse createTraining(CreateTrainingRequest request, UserCredentials credentials) {
        coreValidator.validate(request);
        coreValidator.validate(credentials);

        authenticationService.validateTrainerCredentials(credentials);

        return trainingMapper.toResponse(trainingService.create(trainingMapper.toEntity(request)));
    }

    public Optional<TrainingResponse> findTrainingById(Long id, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateCredentials(credentials);

        return trainingService.findById(id).map(trainingMapper::toResponse);
    }

    public List<TrainingResponse> findAllTrainings(UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateCredentials(credentials);

        return trainingService.findAll().stream()
                .map(trainingMapper::toResponse)
                .toList();
    }

    public List<TrainingResponse> findTrainingsByTraineeCriteria(TraineeTrainingSearchFilter filter, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTraineeCredentials(credentials);

        return trainingService.findByTraineeCriteria(filter).stream()
                .map(trainingMapper::toResponse)
                .toList();
    }

    public List<TrainingResponse> findTrainingsByTrainerCriteria(TrainerTrainingSearchFilter filter, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTrainerCredentials(credentials);

        return trainingService.findByTrainerCriteria(filter).stream()
                .map(trainingMapper::toResponse)
                .toList();
    }
}