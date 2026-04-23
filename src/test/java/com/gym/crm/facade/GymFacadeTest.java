package com.gym.crm.facade;

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
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
                .build();
        when(traineeMapper.toEntity(request)).thenReturn(trainee);
        when(traineeService.update(trainee)).thenReturn(trainee);
        when(traineeMapper.toResponse(trainee)).thenReturn(traineeResponse);

        TraineeResponse actual = facade.updateTrainee(request);

        assertEquals(traineeResponse, actual);
        verify(traineeMapper).toEntity(request);
        verify(traineeService).update(trainee);
        verify(traineeMapper).toResponse(trainee);
    }

    @Test
    void deleteTrainee_whenValidId_callsService() {
        facade.deleteTrainee(EXISTING_ID);

        verify(traineeService).delete(EXISTING_ID);
    }

    @Test
    void findTraineeById_whenTraineeExists_returnsResponse() {
        when(traineeService.findById(EXISTING_ID)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toResponse(trainee)).thenReturn(traineeResponse);

        Optional<TraineeResponse> actual = facade.findTraineeById(EXISTING_ID);

        assertTrue(actual.isPresent());
        assertEquals(traineeResponse, actual.get());
        verify(traineeService).findById(EXISTING_ID);
    }

    @Test
    void findTraineeById_whenTraineeNotExists_returnsEmpty() {
        when(traineeService.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<TraineeResponse> actual = facade.findTraineeById(NON_EXISTING_ID);

        assertFalse(actual.isPresent());
        verify(traineeService).findById(NON_EXISTING_ID);
    }

    @Test
    void findAllTrainees_returnsAllTraineeResponses() {
        when(traineeService.findAll()).thenReturn(List.of(trainee));
        when(traineeMapper.toResponse(trainee)).thenReturn(traineeResponse);

        List<TraineeResponse> actual = facade.findAllTrainees();

        assertEquals(1, actual.size());
        assertEquals(traineeResponse, actual.get(0));
        verify(traineeService).findAll();
    }

    @Test
    void createTrainer_whenValidRequest_returnsTrainerResponse() {
        CreateTrainerRequest request = CreateTrainerRequest.builder()
                .firstName("Mike")
                .lastName("Tyson")
                .build();
        when(trainerMapper.toEntity(request)).thenReturn(trainer);
        when(trainerService.create(trainer)).thenReturn(trainer);
        when(trainerMapper.toResponse(trainer)).thenReturn(trainerResponse);

        TrainerResponse actual = facade.createTrainer(request);

        assertEquals(trainerResponse, actual);
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
                .build();
        when(trainerMapper.toEntity(request)).thenReturn(trainer);
        when(trainerService.update(trainer)).thenReturn(trainer);
        when(trainerMapper.toResponse(trainer)).thenReturn(trainerResponse);

        TrainerResponse actual = facade.updateTrainer(request);

        assertEquals(trainerResponse, actual);
        verify(trainerMapper).toEntity(request);
        verify(trainerService).update(trainer);
        verify(trainerMapper).toResponse(trainer);
    }

    @Test
    void findTrainerById_whenTrainerExists_returnsResponse() {
        when(trainerService.findById(EXISTING_ID)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toResponse(trainer)).thenReturn(trainerResponse);

        Optional<TrainerResponse> actual = facade.findTrainerById(EXISTING_ID);

        assertTrue(actual.isPresent());
        assertEquals(trainerResponse, actual.get());
        verify(trainerService).findById(EXISTING_ID);
    }

    @Test
    void findTrainerById_whenTrainerNotExists_returnsEmpty() {
        when(trainerService.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<TrainerResponse> actual = facade.findTrainerById(NON_EXISTING_ID);

        assertFalse(actual.isPresent());
        verify(trainerService).findById(NON_EXISTING_ID);
    }

    @Test
    void findAllTrainers_returnsAllTrainerResponses() {
        when(trainerService.findAll()).thenReturn(List.of(trainer));
        when(trainerMapper.toResponse(trainer)).thenReturn(trainerResponse);

        List<TrainerResponse> actual = facade.findAllTrainers();

        assertEquals(1, actual.size());
        assertEquals(trainerResponse, actual.get(0));
        verify(trainerService).findAll();
    }

    @Test
    void createTraining_whenValidRequest_returnsTrainingResponse() {
        CreateTrainingRequest request = CreateTrainingRequest.builder()
                .traineeId(EXISTING_ID)
                .trainerId(EXISTING_ID)
                .trainingName("Boxing basics")
                .build();
        when(trainingMapper.toEntity(request)).thenReturn(training);
        when(trainingService.create(training)).thenReturn(training);
        when(trainingMapper.toResponse(training)).thenReturn(trainingResponse);

        TrainingResponse actual = facade.createTraining(request);

        assertEquals(trainingResponse, actual);
        verify(trainingMapper).toEntity(request);
        verify(trainingService).create(training);
        verify(trainingMapper).toResponse(training);
    }

    @Test
    void findTrainingById_whenTrainingExists_returnsResponse() {
        when(trainingService.findById(EXISTING_ID)).thenReturn(Optional.of(training));
        when(trainingMapper.toResponse(training)).thenReturn(trainingResponse);

        Optional<TrainingResponse> actual = facade.findTrainingById(EXISTING_ID);

        assertTrue(actual.isPresent());
        assertEquals(trainingResponse, actual.get());
        verify(trainingService).findById(EXISTING_ID);
    }

    @Test
    void findTrainingById_whenTrainingNotExists_returnsEmpty() {
        when(trainingService.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Optional<TrainingResponse> actual = facade.findTrainingById(NON_EXISTING_ID);

        assertFalse(actual.isPresent());
        verify(trainingService).findById(NON_EXISTING_ID);
    }

    @Test
    void findAllTrainings_returnsAllTrainingResponses() {
        when(trainingService.findAll()).thenReturn(List.of(training));
        when(trainingMapper.toResponse(training)).thenReturn(trainingResponse);

        List<TrainingResponse> actual = facade.findAllTrainings();

        assertEquals(1, actual.size());
        assertEquals(trainingResponse, actual.get(0));
        verify(trainingService).findAll();
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .userId(EXISTING_ID)
                .firstName("Abdul")
                .lastName("Hariton")
                .username("Abdul.Hariton")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Kyiv")
                .isActive(true)
                .build();
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .userId(EXISTING_ID)
                .firstName("Mike")
                .lastName("Tyson")
                .username("Mike.Tyson")
                .specialization(new TrainingType("BOXING"))
                .isActive(true)
                .build();
    }

    private Training buildTraining() {
        return Training.builder()
                .trainingId(EXISTING_ID)
                .traineeId(EXISTING_ID)
                .trainerId(EXISTING_ID)
                .trainingName("Boxing basics")
                .trainingType(new TrainingType("BOXING"))
                .trainingDate(LocalDate.of(2024, 5, 1))
                .trainingDuration(60)
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