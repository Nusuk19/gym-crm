package com.gym.crm.repository;

import com.gym.crm.dao.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.dao.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.repository.specification.TrainingSpecifications;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = "/datasets/training-insert.sql", executionPhase = BEFORE_TEST_METHOD)
class TrainingRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;

    @Test
    void findAllWithAssociations_returnsAllTrainings() {
        assertThat(trainingRepository.findAllWithAssociations()).hasSize(3);
    }

    @Test
    void findById_existingId_returnsTrainingWithAllFields() {
        Long id = getTrainingId("Boxing Basics");

        Optional<Training> result = trainingRepository.findById(id);

        assertThat(result).isPresent();
        Training training = result.get();
        assertThat(training.getName()).isEqualTo("Boxing Basics");
        assertThat(training.getTrainingDate()).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(training.getTrainingDuration()).isEqualByComparingTo(new BigDecimal("60"));
        assertThat(training.getTrainee().getUser().getUsername()).isEqualTo("Abdul.Hariton");
        assertThat(training.getTrainer().getUser().getUsername()).isEqualTo("Mike.Tyson");
        assertThat(training.getTrainingType().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        assertThat(trainingRepository.findById(999L)).isEmpty();
    }

    @Test
    void save_newTraining_persistsAllFields() {
        Training saved = trainingRepository.save(buildTraining(
                "Power Boxing", LocalDate.of(2024, 9, 1), new BigDecimal("75"),
                "Abdul.Hariton", "Mike.Tyson", "Boxing"));
        em.flush();
        em.clear();

        Optional<Training> fromDb = trainingRepository.findById(saved.getId());

        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getName()).isEqualTo("Power Boxing");
        assertThat(fromDb.get().getTrainingDuration()).isEqualByComparingTo(new BigDecimal("75"));
        assertThat(fromDb.get().getTrainee().getUser().getUsername()).isEqualTo("Abdul.Hariton");
        assertThat(fromDb.get().getTrainer().getUser().getUsername()).isEqualTo("Mike.Tyson");
        assertThat(fromDb.get().getTrainingType().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void findByTraineeCriteria_onlyUsername_returnsAllTraineeTrainings() {
        var filter = TraineeTrainingSearchFilter.builder().username("Abdul.Hariton").build();

        List<Training> result = trainingRepository.findAll(TrainingSpecifications.forTraineeCriteria(filter));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(t -> t.getTrainee().getUser().getUsername().equals("Abdul.Hariton"));
    }

    @Test
    void findByTraineeCriteria_withFromDate_returnsFilteredTrainings() {
        var filter = TraineeTrainingSearchFilter.builder()
                .username("Abdul.Hariton")
                .fromDate(LocalDate.of(2024, 8, 1))
                .build();

        List<Training> result = trainingRepository.findAll(TrainingSpecifications.forTraineeCriteria(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Advanced Boxing");
    }

    @Test
    void findByTraineeCriteria_withTrainingType_returnsFilteredTrainings() {
        var filter = TraineeTrainingSearchFilter.builder()
                .username("Abdul.Hariton")
                .trainingTypeName("Boxing")
                .build();

        List<Training> result = trainingRepository.findAll(TrainingSpecifications.forTraineeCriteria(filter));

        assertThat(result).hasSize(2)
                .allMatch(t -> t.getTrainingType().getTrainingTypeName().equals("Boxing"));
    }

    @Test
    void findByTraineeCriteria_noMatch_returnsEmpty() {
        var filter = TraineeTrainingSearchFilter.builder()
                .username("Abdul.Hariton")
                .trainingTypeName("Yoga")
                .build();

        assertThat(trainingRepository.findAll(TrainingSpecifications.forTraineeCriteria(filter))).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("traineeFilterProvider")
    void findByTraineeCriteria_parametrized_returnsExpectedCount(TraineeTrainingSearchFilter filter, int expectedCount) {
        assertThat(trainingRepository.findAll(TrainingSpecifications.forTraineeCriteria(filter))).hasSize(expectedCount);
    }

    static Stream<Arguments> traineeFilterProvider() {
        return Stream.of(
                Arguments.of(TraineeTrainingSearchFilter.builder().username("Abdul.Hariton").build(), 2),
                Arguments.of(TraineeTrainingSearchFilter.builder().username("Abdul.Hariton").fromDate(LocalDate.of(2024, 8, 1)).build(), 1),
                Arguments.of(TraineeTrainingSearchFilter.builder().username("Abdul.Hariton").trainingTypeName("Yoga").build(), 0),
                Arguments.of(TraineeTrainingSearchFilter.builder().username("John.Smith").trainingTypeName("Yoga").build(), 1),
                Arguments.of(TraineeTrainingSearchFilter.builder().username("Abdul.Hariton").trainerFullName("Mike Tyson").build(), 2));
    }

    @Test
    void findByTrainerCriteria_onlyUsername_returnsAllTrainerTrainings() {
        var filter = TrainerTrainingSearchFilter.builder().username("Mike.Tyson").build();

        List<Training> result = trainingRepository.findAll(TrainingSpecifications.forTrainerCriteria(filter));

        assertThat(result).hasSize(2)
                .allMatch(t -> t.getTrainer().getUser().getUsername().equals("Mike.Tyson"));
    }

    @Test
    void findByTrainerCriteria_withTraineeName_returnsFilteredTrainings() {
        var filter = TrainerTrainingSearchFilter.builder()
                .username("Mike.Tyson")
                .traineeFullName("Abdul Hariton")
                .build();

        List<Training> result = trainingRepository.findAll(TrainingSpecifications.forTrainerCriteria(filter));

        assertThat(result).hasSize(2)
                .allMatch(t -> t.getTrainee().getUser().getUsername().equals("Abdul.Hariton"));
    }

    @ParameterizedTest
    @MethodSource("trainerFilterProvider")
    void findByTrainerCriteria_parametrized_returnsExpectedCount(TrainerTrainingSearchFilter filter, int expectedCount) {
        assertThat(trainingRepository.findAll(TrainingSpecifications.forTrainerCriteria(filter))).hasSize(expectedCount);
    }

    static Stream<Arguments> trainerFilterProvider() {
        return Stream.of(
                Arguments.of(TrainerTrainingSearchFilter.builder().username("Mike.Tyson").build(), 2),
                Arguments.of(TrainerTrainingSearchFilter.builder().username("Anna.Jones").build(), 1),
                Arguments.of(TrainerTrainingSearchFilter.builder().username("Mike.Tyson").fromDate(LocalDate.of(2024, 8, 1)).build(), 1),
                Arguments.of(TrainerTrainingSearchFilter.builder().username("Mike.Tyson").traineeFullName("Abdul Hariton").build(), 2),
                Arguments.of(TrainerTrainingSearchFilter.builder().username("Anna.Jones").fromDate(LocalDate.of(2025, 1, 1)).build(), 0));
    }

    private Long getTrainingId(String trainingName) {
        return em.getEntityManager()
                .createQuery("SELECT t.id FROM Training t WHERE t.name = :name", Long.class)
                .setParameter("name", trainingName)
                .getSingleResult();
    }

    private Trainee findTraineeByUsername(String username) {
        return em.getEntityManager()
                .createQuery("FROM Trainee t JOIN FETCH t.user WHERE t.user.username = :u", Trainee.class)
                .setParameter("u", username)
                .getSingleResult();
    }

    private Trainer findTrainerByUsername(String username) {
        return em.getEntityManager()
                .createQuery("FROM Trainer t JOIN FETCH t.user JOIN FETCH t.specialization WHERE t.user.username = :u", Trainer.class)
                .setParameter("u", username)
                .getSingleResult();
    }

    private TrainingType findTrainingTypeByName(String name) {
        return em.getEntityManager()
                .createQuery("FROM TrainingType t WHERE t.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    private Training buildTraining(String name, LocalDate date, BigDecimal duration,
                                   String traineeUsername, String trainerUsername, String typeName) {
        return Training.builder()
                .name(name)
                .trainingDate(date)
                .trainingDuration(duration)
                .trainee(findTraineeByUsername(traineeUsername))
                .trainer(findTrainerByUsername(trainerUsername))
                .trainingType(findTrainingTypeByName(typeName))
                .build();
    }
}