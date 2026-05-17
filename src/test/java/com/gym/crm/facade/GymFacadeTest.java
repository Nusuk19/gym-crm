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
import com.gym.crm.dao.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.dto.request.ActivationRequest;
import com.gym.crm.dto.request.ChangePasswordRequest;
import com.gym.crm.dto.request.CreateTraineeRequest;
import com.gym.crm.dto.request.CreateTrainerRequest;
import com.gym.crm.dto.request.CreateTrainingRequest;
import com.gym.crm.dto.request.UpdateTraineeRequest;
import com.gym.crm.dto.request.UpdateTrainerRequest;
import com.gym.crm.dto.request.UserCredentials;
import com.gym.crm.dto.response.AssignedTrainerInfo;
import com.gym.crm.dto.response.TraineeCreatedResponse;
import com.gym.crm.dto.response.TraineeProfileResponse;
import com.gym.crm.dto.response.TrainerCreatedResponse;
import com.gym.crm.dto.response.TrainerProfileResponse;
import com.gym.crm.dto.response.TrainingResponse;
import com.gym.crm.mapper.AuthMapper;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TraineeRestMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
import com.gym.crm.service.common.AuthenticationService;
import com.gym.crm.service.common.CoreValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    private static final Long EXISTING_ID = 1L;
    private static final Long NON_EXISTING_ID = 99L;
    private static final String USERNAME = "Abdul.Hariton";
    private static final String TRAINER_USERNAME = "Mike.Tyson";

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private UserService userService;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private AuthMapper authMapper;
    @Mock
    private TraineeRestMapper traineeRestMapper;
    @Mock
    private CoreValidator coreValidator;
    @Mock
    private AuthenticationService authenticationService;

    private GymFacade facade;
    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TraineeCreatedResponse traineeCreatedResponse;
    private TraineeProfileResponse traineeProfileResponse;
    private TrainerCreatedResponse trainerCreatedResponse;
    private TrainerProfileResponse trainerProfileResponse;
    private TrainingResponse trainingResponse;

    @BeforeEach
    void setUp() {
        facade = new GymFacade(traineeService, trainerService, trainingService, userService);
        facade.setTraineeMapper(traineeMapper);
        facade.setTrainerMapper(trainerMapper);
        facade.setTrainingMapper(trainingMapper);
        facade.setAuthMapper(authMapper);
        facade.setTraineeRestMapper(traineeRestMapper);
        facade.setValidationService(coreValidator);
        facade.setAuthenticationService(authenticationService);

        trainee = buildTrainee();
        trainer = buildTrainer();
        training = buildTraining();
        traineeCreatedResponse = buildTraineeCreatedResponse();
        traineeProfileResponse = buildTraineeProfileResponse();
        trainerCreatedResponse = buildTrainerCreatedResponse();
        trainerProfileResponse = buildTrainerProfileResponse();
        trainingResponse = buildTrainingResponse();
    }

    @Test
    void login_validatesCredentialsAndAuthenticates() {
        LoginRequest request = new LoginRequest(USERNAME, "oldpassword1");
        UserCredentials credentials = UserCredentials.builder()
                .username(USERNAME)
                .password("oldpassword1")
                .build();

        when(authMapper.toCredentials(request)).thenReturn(credentials);

        facade.login(request);

        verify(authMapper).toCredentials(request);
        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateCredentials(credentials);
    }

    @Test
    void changePassword_validatesRequestThenAuthenticatesThenChanges() {
        LoginChangeRequest request = new LoginChangeRequest(USERNAME, "oldpassword1", "newpassword1");
        ChangePasswordRequest changeRequest = ChangePasswordRequest.builder()
                .username(USERNAME)
                .oldPassword("oldpassword1")
                .newPassword("newpassword1")
                .build();

        when(authMapper.toChangePassword(request)).thenReturn(changeRequest);

        facade.changePassword(request);

        verify(authMapper).toChangePassword(request);
        verify(coreValidator).validate(changeRequest);
        ArgumentCaptor<UserCredentials> captor = ArgumentCaptor.forClass(UserCredentials.class);
        verify(authenticationService).validateCredentials(captor.capture());
        verify(userService).changePassword(changeRequest);
        assertThat(captor.getValue().getUsername()).isEqualTo(USERNAME);
        assertThat(captor.getValue().getPassword()).isEqualTo("oldpassword1");
    }

    @Test
    void createTrainee_whenValidRequest_returnsResponse() {
        TraineeCreateRequest request = new TraineeCreateRequest();
        CreateTraineeRequest internalRequest = CreateTraineeRequest.builder()
                .firstName("Abdul")
                .lastName("Hariton")
                .build();
        TraineeCreateResponse expected = new TraineeCreateResponse();

        when(traineeRestMapper.toCreateRequest(request)).thenReturn(internalRequest);
        when(traineeMapper.toEntity(internalRequest)).thenReturn(trainee);
        when(traineeService.create(trainee)).thenReturn(trainee);
        when(traineeMapper.toCreatedResponse(trainee)).thenReturn(traineeCreatedResponse);
        when(traineeRestMapper.toCreateResponse(traineeCreatedResponse)).thenReturn(expected);

        TraineeCreateResponse actual = facade.createTrainee(request);

        assertEquals(expected, actual);
        verify(traineeRestMapper).toCreateRequest(request);
        verify(coreValidator).validate(internalRequest);
        verify(traineeMapper).toEntity(internalRequest);
        verify(traineeService).create(trainee);
        verify(traineeMapper).toCreatedResponse(trainee);
        verify(traineeRestMapper).toCreateResponse(traineeCreatedResponse);
    }

    @Test
    void getTraineeByUsername_whenExists_returnsResponseWithTrainers() {
        TraineeGetResponse expected = new TraineeGetResponse();
        List<AssignedTrainerInfo> trainerInfos = List.of(buildAssignedTrainerInfo());

        when(traineeService.findByUsername(USERNAME)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toAssignedTrainerInfoList(trainee.getTrainers())).thenReturn(trainerInfos);
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(traineeProfileResponse);
        when(traineeRestMapper.toGetResponse(any(TraineeProfileResponse.class))).thenReturn(expected);

        TraineeGetResponse actual = facade.getTraineeByUsername(USERNAME);

        assertEquals(expected, actual);
        verify(traineeService).findByUsername(USERNAME);
        verify(traineeMapper).toAssignedTrainerInfoList(trainee.getTrainers());
    }

    @Test
    void updateTrainee_whenValidRequest_returnsUpdatedTraineeResponse() {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        UpdateTraineeRequest internalRequest = UpdateTraineeRequest.builder()
                .username(USERNAME)
                .firstName("Abdul")
                .lastName("Hariton")
                .isActive(true)
                .build();
        TraineeUpdateResponse expected = new TraineeUpdateResponse();
        List<AssignedTrainerInfo> trainerInfos = List.of(buildAssignedTrainerInfo());

        when(traineeRestMapper.toUpdateRequest(USERNAME, request)).thenReturn(internalRequest);
        when(traineeMapper.toEntity(internalRequest)).thenReturn(trainee);
        when(traineeService.update(trainee)).thenReturn(trainee);
        when(traineeMapper.toAssignedTrainerInfoList(trainee.getTrainers())).thenReturn(trainerInfos);
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(traineeProfileResponse);
        when(traineeRestMapper.toUpdateResponse(any(TraineeProfileResponse.class))).thenReturn(expected);

        TraineeUpdateResponse actual = facade.updateTrainee(USERNAME, request);

        assertEquals(expected, actual);
        verify(traineeRestMapper).toUpdateRequest(USERNAME, request);
        verify(coreValidator).validate(internalRequest);
        verify(traineeService).update(trainee);
    }

    @Test
    void deleteTrainee_whenValidId_callsService() {
        UserCredentials credentials = buildTraineeCredentials();

        facade.deleteTrainee(EXISTING_ID, credentials);

        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateTraineeCredentials(credentials);
        verify(traineeService).deleteById(EXISTING_ID);
    }

    @Test
    void deleteTraineeByUsername_callsService() {
        facade.deleteTraineeByUsername(USERNAME);

        verify(traineeService).deleteByUsername(USERNAME);
    }

    @Test
    void findTraineeById_whenTraineeExists_returnsResponse() {
        UserCredentials credentials = buildTraineeCredentials();

        when(traineeService.findById(EXISTING_ID)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(traineeProfileResponse);

        Optional<TraineeProfileResponse> actual = facade.findTraineeById(EXISTING_ID, credentials);

        assertTrue(actual.isPresent());
        assertEquals(traineeProfileResponse, actual.get());
        verify(authenticationService).validateTraineeCredentials(credentials);
    }

    @Test
    void findTraineeById_whenTraineeNotExists_returnsEmpty() {
        UserCredentials credentials = buildTraineeCredentials();

        when(traineeService.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<TraineeProfileResponse> actual = facade.findTraineeById(NON_EXISTING_ID, credentials);

        assertFalse(actual.isPresent());
        verify(traineeService).findById(NON_EXISTING_ID);
    }

    @Test
    void findTraineeByUsername_whenExists_returnsResponse() {
        UserCredentials credentials = buildTraineeCredentials();

        when(traineeService.findByUsername(USERNAME)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(traineeProfileResponse);

        Optional<TraineeProfileResponse> actual = facade.findTraineeByUsername(USERNAME, credentials);

        assertTrue(actual.isPresent());
        assertEquals(traineeProfileResponse, actual.get());
        verify(traineeService).findByUsername(USERNAME);
    }

    @Test
    void findTraineeByUsername_whenNotExists_returnsEmpty() {
        UserCredentials credentials = buildTraineeCredentials();

        when(traineeService.findByUsername(USERNAME)).thenReturn(Optional.empty());

        Optional<TraineeProfileResponse> actual = facade.findTraineeByUsername(USERNAME, credentials);

        assertFalse(actual.isPresent());
    }

    @Test
    void findAllTrainees_returnsAllTraineeResponses() {
        UserCredentials credentials = buildTraineeCredentials();

        when(traineeService.findAll()).thenReturn(List.of(trainee));
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(traineeProfileResponse);

        List<TraineeProfileResponse> actual = facade.findAllTrainees(credentials);

        assertEquals(1, actual.size());
        assertEquals(traineeProfileResponse, actual.get(0));
        verify(traineeService).findAll();
    }

    @Test
    void updateTraineeTrainers_callsService() {
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest();
        request.setTrainerUsernames(List.of("Mike.Tyson", "John.Doe"));
        AssignedTrainerInfo info = buildAssignedTrainerInfo();
        TraineeAssignedTrainersUpdateResponse expected = new TraineeAssignedTrainersUpdateResponse();

        when(traineeService.updateTrainers(USERNAME, request.getTrainerUsernames())).thenReturn(List.of(trainer));
        when(traineeMapper.toAssignedTrainerInfo(trainer)).thenReturn(info);
        when(traineeRestMapper.toAssignedTrainersUpdateResponse(List.of(info))).thenReturn(expected);

        TraineeAssignedTrainersUpdateResponse actual = facade.updateTraineeTrainers(USERNAME, request);

        assertEquals(expected, actual);
        verify(traineeService).updateTrainers(USERNAME, request.getTrainerUsernames());
    }

    @Test
    void changeTraineeActivationStatus_whenActive_callsActivate() {
        ActivationStatusRequest body = new ActivationStatusRequest(true);
        ActivationRequest activation = ActivationRequest.builder()
                .username(USERNAME)
                .isActive(true)
                .build();

        when(traineeRestMapper.toActivationRequest(USERNAME, body)).thenReturn(activation);

        facade.changeTraineeActivationStatus(USERNAME, body);

        verify(traineeService).activate(activation);
    }

    @Test
    void changeTraineeActivationStatus_whenInactive_callsDeactivate() {
        ActivationStatusRequest body = new ActivationStatusRequest(false);
        ActivationRequest activation = ActivationRequest.builder()
                .username(USERNAME)
                .isActive(false)
                .build();

        when(traineeRestMapper.toActivationRequest(USERNAME, body)).thenReturn(activation);

        facade.changeTraineeActivationStatus(USERNAME, body);

        verify(traineeService).deactivate(activation);
    }

    @Test
    void changeTraineePassword_callsServiceWithRequest() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .username(USERNAME)
                .oldPassword("oldPass123")
                .newPassword("newPass456")
                .build();
        UserCredentials credentials = buildTraineeCredentials();

        facade.changeTraineePassword(request, credentials);

        verify(authenticationService).validateTraineeCredentials(credentials);
        verify(traineeService).changePassword(request);
    }

    @Test
    void activateTrainee_callsServiceWithRequest() {
        ActivationRequest request = ActivationRequest.builder()
                .username(USERNAME)
                .build();
        UserCredentials credentials = buildTraineeCredentials();

        facade.activateTrainee(request, credentials);

        verify(coreValidator).validate(request);
        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateTraineeCredentials(credentials);
        verify(traineeService).activate(request);
    }

    @Test
    void deactivateTrainee_callsServiceWithRequest() {
        ActivationRequest request = ActivationRequest.builder()
                .username(USERNAME)
                .build();
        UserCredentials credentials = buildTraineeCredentials();

        facade.deactivateTrainee(request, credentials);

        verify(coreValidator).validate(request);
        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateTraineeCredentials(credentials);
        verify(traineeService).deactivate(request);
    }

    @Test
    void createTrainer_whenValidRequest_returnsTrainerCreatedResponse() {
        CreateTrainerRequest request = CreateTrainerRequest.builder()
                .firstName("Mike")
                .lastName("Tyson")
                .specializationId(EXISTING_ID)
                .build();

        when(trainerMapper.toEntity(request)).thenReturn(trainer);
        when(trainerService.create(trainer)).thenReturn(trainer);
        when(trainerMapper.toCreatedResponse(trainer)).thenReturn(trainerCreatedResponse);

        TrainerCreatedResponse actual = facade.createTrainer(request);

        assertEquals(trainerCreatedResponse, actual);
        verify(coreValidator).validate(request);
        verify(trainerMapper).toEntity(request);
        verify(trainerService).create(trainer);
        verify(trainerMapper).toCreatedResponse(trainer);
    }

    @Test
    void updateTrainer_whenValidRequest_returnsUpdatedTrainerResponse() {
        UpdateTrainerRequest request = UpdateTrainerRequest.builder()
                .username(TRAINER_USERNAME)
                .firstName("Mike")
                .lastName("Tyson")
                .username(TRAINER_USERNAME)
                .specializationId(EXISTING_ID)
                .build();
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerMapper.toEntity(request)).thenReturn(trainer);
        when(trainerService.update(trainer)).thenReturn(trainer);
        when(trainerMapper.toProfileResponse(trainer)).thenReturn(trainerProfileResponse);

        TrainerProfileResponse actual = facade.updateTrainer(request, credentials);

        assertEquals(trainerProfileResponse, actual);
        verify(coreValidator).validate(request);
        verify(authenticationService).validateTrainerCredentials(credentials);
        verify(trainerService).update(trainer);
    }

    @Test
    void findTrainerById_whenTrainerExists_returnsResponse() {
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerService.findById(EXISTING_ID)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toProfileResponse(trainer)).thenReturn(trainerProfileResponse);

        Optional<TrainerProfileResponse> actual = facade.findTrainerById(EXISTING_ID, credentials);

        assertTrue(actual.isPresent());
        assertEquals(trainerProfileResponse, actual.get());
        verify(authenticationService).validateTrainerCredentials(credentials);
    }

    @Test
    void findTrainerById_whenTrainerNotExists_returnsEmpty() {
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerService.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<TrainerProfileResponse> actual = facade.findTrainerById(NON_EXISTING_ID, credentials);

        assertFalse(actual.isPresent());
        verify(trainerService).findById(NON_EXISTING_ID);
    }

    @Test
    void findTrainerByUsername_whenExists_returnsResponse() {
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerService.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toProfileResponse(trainer)).thenReturn(trainerProfileResponse);

        Optional<TrainerProfileResponse> actual = facade.findTrainerByUsername(TRAINER_USERNAME, credentials);

        assertTrue(actual.isPresent());
        assertEquals(trainerProfileResponse, actual.get());
        verify(trainerService).findByUsername(TRAINER_USERNAME);
    }

    @Test
    void findTrainerByUsername_whenNotExists_returnsEmpty() {
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerService.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.empty());

        Optional<TrainerProfileResponse> actual = facade.findTrainerByUsername(TRAINER_USERNAME, credentials);

        assertFalse(actual.isPresent());
    }

    @Test
    void findAllTrainers_returnsAllTrainerResponses() {
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerService.findAll()).thenReturn(List.of(trainer));
        when(trainerMapper.toProfileResponse(trainer)).thenReturn(trainerProfileResponse);

        List<TrainerProfileResponse> actual = facade.findAllTrainers(credentials);

        assertEquals(1, actual.size());
        assertEquals(trainerProfileResponse, actual.get(0));
        verify(trainerService).findAll();
    }

    @Test
    void findAllTrainersNotAssignedToTrainee_returnsFilteredList() {
        AssignedTrainerInfo info = buildAssignedTrainerInfo();
        AssignedTrainerResponse assigned = new AssignedTrainerResponse();

        when(trainerService.findAllNotAssignedToTrainee(USERNAME)).thenReturn(List.of(trainer));
        when(traineeMapper.toAssignedTrainerInfo(trainer)).thenReturn(info);
        when(traineeRestMapper.toAssignedTrainerResponse(info)).thenReturn(assigned);

        List<AssignedTrainerResponse> actual = facade.findAllTrainersNotAssignedToTrainee(USERNAME);

        assertEquals(1, actual.size());
        verify(trainerService).findAllNotAssignedToTrainee(USERNAME);
    }

    @Test
    void changeTrainerPassword_callsServiceWithRequest() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .username(TRAINER_USERNAME)
                .oldPassword("oldPass123")
                .newPassword("newPass456")
                .build();
        UserCredentials credentials = buildTrainerCredentials();

        facade.changeTrainerPassword(request, credentials);

        verify(coreValidator).validate(request);
        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateTrainerCredentials(credentials);
        verify(trainerService).changePassword(request);
    }

    @Test
    void activateTrainer_callsServiceWithRequest() {
        ActivationRequest request = ActivationRequest.builder()
                .username(TRAINER_USERNAME)
                .build();
        UserCredentials credentials = buildTrainerCredentials();

        facade.activateTrainer(request, credentials);

        verify(coreValidator).validate(request);
        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateTrainerCredentials(credentials);
        verify(trainerService).activate(request);
    }

    @Test
    void deactivateTrainer_callsServiceWithRequest() {
        ActivationRequest request = ActivationRequest.builder()
                .username(TRAINER_USERNAME)
                .build();
        UserCredentials credentials = buildTrainerCredentials();

        facade.deactivateTrainer(request, credentials);

        verify(coreValidator).validate(request);
        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateTrainerCredentials(credentials);
        verify(trainerService).deactivate(request);
    }

    @Test
    void createTraining_whenValidRequest_returnsTrainingResponse() {
        CreateTrainingRequest request = CreateTrainingRequest.builder()
                .traineeId(EXISTING_ID)
                .trainerId(EXISTING_ID)
                .trainingName("Boxing basics")
                .build();
        UserCredentials credentials = buildTrainerCredentials();

        when(trainingMapper.toEntity(request)).thenReturn(training);
        when(trainingService.create(training)).thenReturn(training);
        when(trainingMapper.toResponse(training)).thenReturn(trainingResponse);

        TrainingResponse actual = facade.createTraining(request, credentials);

        assertEquals(trainingResponse, actual);
        verify(coreValidator).validate(request);
        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateTrainerCredentials(credentials);
        verify(trainingMapper).toEntity(request);
        verify(trainingService).create(training);
        verify(trainingMapper).toResponse(training);
    }

    @Test
    void findTrainingById_whenTrainingExists_returnsResponse() {
        UserCredentials credentials = buildTraineeCredentials();

        when(trainingService.findById(EXISTING_ID)).thenReturn(Optional.of(training));
        when(trainingMapper.toResponse(training)).thenReturn(trainingResponse);

        Optional<TrainingResponse> actual = facade.findTrainingById(EXISTING_ID, credentials);

        assertTrue(actual.isPresent());
        assertEquals(trainingResponse, actual.get());
        verify(authenticationService).validateCredentials(credentials);
        verify(trainingService).findById(EXISTING_ID);
    }

    @Test
    void findTrainingById_whenTrainingNotExists_returnsEmpty() {
        UserCredentials credentials = buildTraineeCredentials();

        when(trainingService.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<TrainingResponse> actual = facade.findTrainingById(NON_EXISTING_ID, credentials);

        assertFalse(actual.isPresent());
        verify(trainingService).findById(NON_EXISTING_ID);
    }

    @Test
    void findAllTrainings_returnsAllTrainingResponses() {
        UserCredentials credentials = buildTraineeCredentials();

        when(trainingService.findAll()).thenReturn(List.of(training));
        when(trainingMapper.toResponse(training)).thenReturn(trainingResponse);

        List<TrainingResponse> actual = facade.findAllTrainings(credentials);

        assertEquals(1, actual.size());
        assertEquals(trainingResponse, actual.get(0));
        verify(authenticationService).validateCredentials(credentials);
        verify(trainingService).findAll();
    }

    @Test
    void findTrainingsByTraineeCriteria_returnsFilteredList() {
        GetTraineeTrainingResponse response = new GetTraineeTrainingResponse();

        when(trainingService.findByTraineeCriteria(any())).thenReturn(List.of(training));
        when(trainingMapper.toResponse(training)).thenReturn(trainingResponse);
        when(traineeRestMapper.toTraineeTrainingResponse(trainingResponse)).thenReturn(response);

        List<GetTraineeTrainingResponse> actual = facade.findTrainingsByTraineeCriteria(
                USERNAME, null, null, null, null);

        assertEquals(1, actual.size());
        verify(trainingService).findByTraineeCriteria(any());
    }

    @Test
    void findTrainingsByTrainerCriteria_returnsFilteredList() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(TRAINER_USERNAME)
                .build();
        UserCredentials credentials = buildTrainerCredentials();

        when(trainingService.findByTrainerCriteria(filter)).thenReturn(List.of(training));
        when(trainingMapper.toResponse(training)).thenReturn(trainingResponse);

        List<TrainingResponse> actual = facade.findTrainingsByTrainerCriteria(filter, credentials);

        assertEquals(1, actual.size());
        assertEquals(trainingResponse, actual.get(0));
        verify(authenticationService).validateTrainerCredentials(credentials);
        verify(trainingService).findByTrainerCriteria(filter);
    }

    private UserCredentials buildTraineeCredentials() {
        return UserCredentials.builder()
                .username(USERNAME)
                .password("password123")
                .build();
    }

    private UserCredentials buildTrainerCredentials() {
        return UserCredentials.builder()
                .username(TRAINER_USERNAME)
                .password("password123")
                .build();
    }

    private Trainee buildTrainee() {
        User user = User.builder()
                .id(EXISTING_ID)
                .firstName("Abdul")
                .lastName("Hariton")
                .username("Abdul.Hariton")
                .isActive(true)
                .build();
        return Trainee.builder()
                .id(EXISTING_ID)
                .user(user)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Kyiv")
                .build();
    }

    private Trainer buildTrainer() {
        User user = User.builder()
                .id(EXISTING_ID)
                .firstName("Mike")
                .lastName("Tyson")
                .username("Mike.Tyson")
                .isActive(true)
                .build();
        return Trainer.builder()
                .id(EXISTING_ID)
                .user(user)
                .specialization(TrainingType.builder().trainingTypeName("BOXING").build())
                .build();
    }

    private Training buildTraining() {
        return Training.builder()
                .id(EXISTING_ID)
                .name("Boxing basics")
                .trainee(buildTrainee())
                .trainer(buildTrainer())
                .trainingType(TrainingType.builder().trainingTypeName("BOXING").build())
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(BigDecimal.valueOf(60))
                .build();
    }

    private TraineeCreatedResponse buildTraineeCreatedResponse() {
        return TraineeCreatedResponse.builder()
                .username(USERNAME)
                .password("rawPass123")
                .build();
    }

    private TraineeProfileResponse buildTraineeProfileResponse() {
        return TraineeProfileResponse.builder()
                .id(EXISTING_ID)
                .username(USERNAME)
                .firstName("Abdul")
                .lastName("Hariton")
                .isActive(true)
                .build();
    }

    private TrainerCreatedResponse buildTrainerCreatedResponse() {
        return TrainerCreatedResponse.builder()
                .username(TRAINER_USERNAME)
                .password("rawPass123")
                .build();
    }

    private TrainerProfileResponse buildTrainerProfileResponse() {
        return TrainerProfileResponse.builder()
                .id(EXISTING_ID)
                .username(TRAINER_USERNAME)
                .firstName("Mike")
                .lastName("Tyson")
                .specialization(TrainingType.builder().trainingTypeName("BOXING").build())
                .isActive(true)
                .build();
    }

    private TrainingResponse buildTrainingResponse() {
        return TrainingResponse.builder()
                .id(EXISTING_ID)
                .traineeId(EXISTING_ID)
                .trainerId(EXISTING_ID)
                .trainingName("Boxing basics")
                .build();
    }

    private AssignedTrainerInfo buildAssignedTrainerInfo() {
        return AssignedTrainerInfo.builder()
                .username(TRAINER_USERNAME)
                .firstName("Mike")
                .lastName("Tyson")
                .specialization(TrainingType.builder().trainingTypeName("BOXING").build())
                .build();
    }
}