package com.gym.crm.facade;

import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
import com.gym.crm.dao.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.dao.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.dto.request.ActivationRequest;
import com.gym.crm.dto.request.ChangePasswordRequest;
import com.gym.crm.dto.request.CreateTraineeRequest;
import com.gym.crm.dto.request.CreateTrainerRequest;
import com.gym.crm.dto.request.CreateTrainingRequest;
import com.gym.crm.dto.request.UpdateTrainerRequest;
import com.gym.crm.dto.request.UserCredentials;
import com.gym.crm.dto.response.AssignedTrainerInfo;
import com.gym.crm.dto.response.TraineeCreatedResponse;
import com.gym.crm.dto.response.TraineeProfileResponse;
import com.gym.crm.dto.response.TrainerCreatedResponse;
import com.gym.crm.dto.response.TrainerProfileResponse;
import com.gym.crm.dto.response.TrainingResponse;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.mapper.AuthMapper;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TraineeRestMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.security.Authenticated;
import com.gym.crm.security.SecurityContext;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
import com.gym.crm.service.common.AuthenticationService;
import com.gym.crm.service.common.CoreValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;

    private TraineeMapper traineeMapper;
    private TraineeRestMapper traineeRestMapper;
    private TrainerMapper trainerMapper;
    private TrainingMapper trainingMapper;
    private AuthMapper authMapper;
    private CoreValidator coreValidator;
    private AuthenticationService authenticationService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService,
                     UserService userService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.userService = userService;
    }

    @Autowired
    public void setTraineeMapper(TraineeMapper traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    @Autowired
    public void setTraineeRestMapper(TraineeRestMapper traineeRestMapper) {
        this.traineeRestMapper = traineeRestMapper;
    }

    @Autowired
    public void setAuthMapper(AuthMapper authMapper) {
        this.authMapper = authMapper;
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

    public void login(LoginRequest request) {
        UserCredentials credentials = authMapper.toCredentials(request);
        coreValidator.validate(credentials);

        authenticationService.validateCredentials(credentials);
        SecurityContext.setCurrentUser(credentials.getUsername());
    }

    public void changePassword(LoginChangeRequest request) {
        ChangePasswordRequest changeRequest = authMapper.toChangePassword(request);
        coreValidator.validate(changeRequest);

        UserCredentials credentials = UserCredentials.builder()
                .username(changeRequest.getUsername())
                .password(changeRequest.getOldPassword())
                .build();

        authenticationService.validateCredentials(credentials);
        userService.changePassword(changeRequest);
    }

    public TraineeCreateResponse createTrainee(TraineeCreateRequest request) {
        CreateTraineeRequest internalRequest = traineeRestMapper.toCreateRequest(request);
        coreValidator.validate(internalRequest);

        TraineeCreatedResponse response = traineeMapper.toCreatedResponse(
                traineeService.create(traineeMapper.toEntity(internalRequest)));

        return traineeRestMapper.toCreateResponse(response);
    }

    @Authenticated
    public TraineeGetResponse getTraineeByUsername(String username) {

        TraineeProfileResponse traineeProfile = traineeService.findByUsername(username)
                .map(traineeMapper::toProfileResponse)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));

        return traineeRestMapper.toGetResponse(traineeProfile);
    }

    @Authenticated
    public TraineeUpdateResponse updateTrainee(String username, TraineeUpdateRequest request) {
        var internalRequest = traineeRestMapper.toUpdateRequest(username, request);
        coreValidator.validate(internalRequest);

        var updated = traineeService.update(traineeMapper.toEntity(internalRequest));
        List<AssignedTrainerInfo> assignedTrainers =
                traineeMapper.toAssignedTrainerInfoList(updated.getTrainers());

        var profile = traineeMapper.toProfileResponse(updated).toBuilder()
                .trainers(assignedTrainers)
                .build();

        return traineeRestMapper.toUpdateResponse(profile);
    }

    public void deleteTrainee(Long id, UserCredentials credentials) {
        coreValidator.validate(id);
        coreValidator.validate(credentials);

        authenticationService.validateTraineeCredentials(credentials);

        traineeService.deleteById(id);
    }

    @Authenticated
    public void deleteTraineeByUsername(String username) {
        traineeService.deleteByUsername(username);
    }

    public Optional<TraineeProfileResponse> findTraineeById(Long id, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTraineeCredentials(credentials);

        return traineeService.findById(id).map(traineeMapper::toProfileResponse);
    }

    public Optional<TraineeProfileResponse> findTraineeByUsername(String username, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTraineeCredentials(credentials);

        return traineeService.findByUsername(username).map(traineeMapper::toProfileResponse);
    }

    public List<TraineeProfileResponse> findAllTrainees(UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTraineeCredentials(credentials);

        return traineeService.findAll().stream()
                .map(traineeMapper::toProfileResponse)
                .toList();
    }

    @Authenticated
    public TraineeAssignedTrainersUpdateResponse updateTraineeTrainers(String username, TraineeAssignedTrainersUpdateRequest request) {
        List<AssignedTrainerInfo> trainers = traineeService.updateTrainers(username, request.getTrainerUsernames()).stream()
                .map(traineeMapper::toAssignedTrainerInfo)
                .toList();

        return traineeRestMapper.toAssignedTrainersUpdateResponse(trainers);
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

    @Authenticated
    public void changeTraineeActivationStatus(String username, ActivationStatusRequest request) {
        ActivationRequest activationRequest = traineeRestMapper.toActivationRequest(username, request);

        Consumer<ActivationRequest> action = request.getIsActive()
                ? traineeService::activate
                : traineeService::deactivate;

        action.accept(activationRequest);
    }


    public TrainerCreatedResponse createTrainer(CreateTrainerRequest request) {
        coreValidator.validate(request);

        return trainerMapper.toCreatedResponse(trainerService.create(trainerMapper.toEntity(request)));
    }

    public TrainerProfileResponse updateTrainer(UpdateTrainerRequest request, UserCredentials credentials) {
        coreValidator.validate(request);
        coreValidator.validate(credentials);

        authenticationService.validateTrainerCredentials(credentials);

        return trainerMapper.toProfileResponse(trainerService.update(trainerMapper.toEntity(request)));
    }

    public Optional<TrainerProfileResponse> findTrainerById(Long id, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTrainerCredentials(credentials);

        return trainerService.findById(id).map(trainerMapper::toProfileResponse);
    }

    public Optional<TrainerProfileResponse> findTrainerByUsername(String username, UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTrainerCredentials(credentials);

        return trainerService.findByUsername(username).map(trainerMapper::toProfileResponse);
    }

    public List<TrainerProfileResponse> findAllTrainers(UserCredentials credentials) {
        coreValidator.validate(credentials);
        authenticationService.validateTrainerCredentials(credentials);

        return trainerService.findAll().stream()
                .map(trainerMapper::toProfileResponse)
                .toList();
    }

    @Authenticated
    public List<AssignedTrainerResponse> findAllTrainersNotAssignedToTrainee(String username) {
        return trainerService.findAllNotAssignedToTrainee(username).stream()
                .map(traineeMapper::toAssignedTrainerInfo)
                .map(traineeRestMapper::toAssignedTrainerResponse)
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

    @Authenticated
    public List<GetTraineeTrainingResponse> findTrainingsByTraineeCriteria(String username, LocalDate fromDate, LocalDate toDate,
                                                                           String trainerName, String trainingType) {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerFullName(trainerName)
                .trainingTypeName(trainingType)
                .build();

        return trainingService.findByTraineeCriteria(filter).stream()
                .map(trainingMapper::toResponse)
                .map(traineeRestMapper::toTraineeTrainingResponse)
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