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
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.AuthenticationService;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.common.CoreValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private TraineeMapper traineeMapper;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private CoreValidator coreValidator;
    @Mock
    private AuthenticationService authenticationService;

    private GymFacade facade;
    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TraineeResponse traineeResponse;
    private TrainerResponse trainerResponse;
    private TrainingResponse trainingResponse;

    @BeforeEach
    void setUp() {
        facade = new GymFacade(traineeService, trainerService, trainingService);
        facade.setTraineeMapper(traineeMapper);
        facade.setTrainerMapper(trainerMapper);
        facade.setTrainingMapper(trainingMapper);
        facade.setValidationService(coreValidator);
        facade.setAuthenticationService(authenticationService);

        trainee = buildTrainee();
        trainer = buildTrainer();
        training = buildTraining();
        traineeResponse = buildTraineeResponse();
        trainerResponse = buildTrainerResponse();
        trainingResponse = buildTrainingResponse();
    }

    @Test
    void createTrainee_whenValidRequest_returnsTraineeResponse() {
        CreateTraineeRequest request = CreateTraineeRequest.builder()
                .firstName("Abdul")
                .lastName("Hariton")
                .build();
        when(traineeMapper.toEntity(request)).thenReturn(trainee);
        when(traineeService.create(trainee)).thenReturn(trainee);
        when(traineeMapper.toResponse(trainee)).thenReturn(traineeResponse);

        TraineeResponse actual = facade.createTrainee(request);

        assertEquals(traineeResponse, actual);
        verify(coreValidator).validate(request);
        verify(traineeMapper).toEntity(request);
        verify(traineeService).create(trainee);
        verify(traineeMapper).toResponse(trainee);
    }

    @Test
    void updateTrainee_whenValidRequest_returnsUpdatedTraineeResponse() {
        UpdateTraineeRequest request = UpdateTraineeRequest.builder()
                .id(EXISTING_ID)
                .firstName("Abdul")
                .lastName("Hariton")
                .username(USERNAME)
                .build();
        UserCredentials credentials = buildTraineeCredentials();

        when(traineeMapper.toEntity(request)).thenReturn(trainee);
        when(traineeService.update(trainee)).thenReturn(trainee);
        when(traineeMapper.toResponse(trainee)).thenReturn(traineeResponse);

        TraineeResponse actual = facade.updateTrainee(request, credentials);

        assertEquals(traineeResponse, actual);
        verify(coreValidator).validate(request);
        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateTraineeCredentials(credentials);
        verify(traineeMapper).toEntity(request);
        verify(traineeService).update(trainee);
        verify(traineeMapper).toResponse(trainee);
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
        UserCredentials credentials = buildTraineeCredentials();

        facade.deleteTraineeByUsername(USERNAME, credentials);

        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateTraineeCredentials(credentials);
        verify(traineeService).deleteByUsername(USERNAME);
    }

    @Test
    void findTraineeById_whenTraineeExists_returnsResponse() {
        UserCredentials credentials = buildTraineeCredentials();

        when(traineeService.findById(EXISTING_ID)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toResponse(trainee)).thenReturn(traineeResponse);

        Optional<TraineeResponse> actual = facade.findTraineeById(EXISTING_ID, credentials);

        assertTrue(actual.isPresent());
        assertEquals(traineeResponse, actual.get());
        verify(authenticationService).validateTraineeCredentials(credentials);
        verify(traineeService).findById(EXISTING_ID);
    }

    @Test
    void findTraineeById_whenTraineeNotExists_returnsEmpty() {
        UserCredentials credentials = buildTraineeCredentials();

        when(traineeService.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<TraineeResponse> actual = facade.findTraineeById(NON_EXISTING_ID, credentials);

        assertFalse(actual.isPresent());
        verify(traineeService).findById(NON_EXISTING_ID);
    }

    @Test
    void findTraineeByUsername_whenExists_returnsResponse() {
        UserCredentials credentials = buildTraineeCredentials();

        when(traineeService.findByUsername(USERNAME)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toResponse(trainee)).thenReturn(traineeResponse);

        Optional<TraineeResponse> actual = facade.findTraineeByUsername(USERNAME, credentials);

        assertTrue(actual.isPresent());
        assertEquals(traineeResponse, actual.get());
        verify(traineeService).findByUsername(USERNAME);
    }

    @Test
    void findTraineeByUsername_whenNotExists_returnsEmpty() {
        UserCredentials credentials = buildTraineeCredentials();

        when(traineeService.findByUsername(USERNAME)).thenReturn(Optional.empty());

        Optional<TraineeResponse> actual = facade.findTraineeByUsername(USERNAME, credentials);

        assertFalse(actual.isPresent());
    }

    @Test
    void findAllTrainees_returnsAllTraineeResponses() {
        UserCredentials credentials = buildTraineeCredentials();

        when(traineeService.findAll()).thenReturn(List.of(trainee));
        when(traineeMapper.toResponse(trainee)).thenReturn(traineeResponse);

        List<TraineeResponse> actual = facade.findAllTrainees(credentials);

        assertEquals(1, actual.size());
        assertEquals(traineeResponse, actual.get(0));
        verify(traineeService).findAll();
    }

    @Test
    void updateTraineeTrainers_callsService() {
        List<String> trainerUsernames = List.of("Mike.Tyson", "John.Doe");
        UserCredentials credentials = buildTraineeCredentials();

        facade.updateTraineeTrainers(USERNAME, trainerUsernames, credentials);

        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateTraineeCredentials(credentials);
        verify(traineeService).updateTrainers(USERNAME, trainerUsernames);
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

        verify(coreValidator).validate(request);
        verify(coreValidator).validate(credentials);
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
        ;
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
    void createTrainer_whenValidRequest_returnsTrainerResponse() {
        CreateTrainerRequest request = CreateTrainerRequest.builder()
                .firstName("Mike")
                .lastName("Tyson")
                .specializationId(EXISTING_ID)
                .build();
        when(trainerMapper.toEntity(request)).thenReturn(trainer);
        when(trainerService.create(trainer)).thenReturn(trainer);
        when(trainerMapper.toResponse(trainer)).thenReturn(trainerResponse);

        TrainerResponse actual = facade.createTrainer(request);

        assertEquals(trainerResponse, actual);
        verify(coreValidator).validate(request);
        verify(trainerMapper).toEntity(request);
        verify(trainerService).create(trainer);
        verify(trainerMapper).toResponse(trainer);
    }

    @Test
    void updateTrainer_whenValidRequest_returnsUpdatedTrainerResponse() {
        UpdateTrainerRequest request = UpdateTrainerRequest.builder()
                .id(EXISTING_ID)
                .firstName("Mike")
                .lastName("Tyson")
                .username(TRAINER_USERNAME)
                .specializationId(EXISTING_ID)
                .build();
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerMapper.toEntity(request)).thenReturn(trainer);
        when(trainerService.update(trainer)).thenReturn(trainer);
        when(trainerMapper.toResponse(trainer)).thenReturn(trainerResponse);

        TrainerResponse actual = facade.updateTrainer(request, credentials);

        assertEquals(trainerResponse, actual);
        verify(coreValidator).validate(request);
        verify(coreValidator).validate(credentials);
        verify(authenticationService).validateTrainerCredentials(credentials);
        verify(trainerMapper).toEntity(request);
        verify(trainerService).update(trainer);
        verify(trainerMapper).toResponse(trainer);
    }

    @Test
    void findTrainerById_whenTrainerExists_returnsResponse() {
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerService.findById(EXISTING_ID)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toResponse(trainer)).thenReturn(trainerResponse);

        Optional<TrainerResponse> actual = facade.findTrainerById(EXISTING_ID, credentials);

        assertTrue(actual.isPresent());
        assertEquals(trainerResponse, actual.get());
        verify(authenticationService).validateTrainerCredentials(credentials);
        verify(trainerService).findById(EXISTING_ID);
    }

    @Test
    void findTrainerById_whenTrainerNotExists_returnsEmpty() {
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerService.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<TrainerResponse> actual = facade.findTrainerById(NON_EXISTING_ID, credentials);

        assertFalse(actual.isPresent());
        verify(trainerService).findById(NON_EXISTING_ID);
    }

    @Test
    void findTrainerByUsername_whenExists_returnsResponse() {
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerService.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toResponse(trainer)).thenReturn(trainerResponse);

        Optional<TrainerResponse> actual = facade.findTrainerByUsername(TRAINER_USERNAME, credentials);

        assertTrue(actual.isPresent());
        assertEquals(trainerResponse, actual.get());
        verify(trainerService).findByUsername(TRAINER_USERNAME);
    }

    @Test
    void findTrainerByUsername_whenNotExists_returnsEmpty() {
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerService.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.empty());

        Optional<TrainerResponse> actual = facade.findTrainerByUsername(TRAINER_USERNAME, credentials);

        assertFalse(actual.isPresent());
    }

    @Test
    void findAllTrainers_returnsAllTrainerResponses() {
        UserCredentials credentials = buildTrainerCredentials();

        when(trainerService.findAll()).thenReturn(List.of(trainer));
        when(trainerMapper.toResponse(trainer)).thenReturn(trainerResponse);

        List<TrainerResponse> actual = facade.findAllTrainers(credentials);

        assertEquals(1, actual.size());
        assertEquals(trainerResponse, actual.get(0));
        verify(trainerService).findAll();
    }

    @Test
    void findAllTrainersNotAssignedToTrainee_returnsFilteredList() {
        UserCredentials credentials = buildTraineeCredentials();

        when(trainerService.findAllNotAssignedToTrainee(USERNAME)).thenReturn(List.of(trainer));
        when(trainerMapper.toResponse(trainer)).thenReturn(trainerResponse);

        List<TrainerResponse> actual = facade.findAllTrainersNotAssignedToTrainee(USERNAME, credentials);

        assertEquals(1, actual.size());
        assertEquals(trainerResponse, actual.get(0));
        verify(authenticationService).validateTraineeCredentials(credentials);
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
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(USERNAME)
                .build();
        UserCredentials credentials = buildTraineeCredentials();

        when(trainingService.findByTraineeCriteria(filter)).thenReturn(List.of(training));
        when(trainingMapper.toResponse(training)).thenReturn(trainingResponse);

        List<TrainingResponse> actual = facade.findTrainingsByTraineeCriteria(filter, credentials);

        assertEquals(1, actual.size());
        assertEquals(trainingResponse, actual.get(0));
        verify(authenticationService).validateTraineeCredentials(credentials);
        verify(trainingService).findByTraineeCriteria(filter);
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

    private TraineeResponse buildTraineeResponse() {
        return TraineeResponse.builder()
                .id(EXISTING_ID)
                .firstName("Abdul")
                .lastName("Hariton")
                .username("Abdul.Hariton")
                .build();
    }

    private TrainerResponse buildTrainerResponse() {
        return TrainerResponse.builder()
                .id(EXISTING_ID)
                .firstName("Mike")
                .lastName("Tyson")
                .username("Mike.Tyson")
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
}