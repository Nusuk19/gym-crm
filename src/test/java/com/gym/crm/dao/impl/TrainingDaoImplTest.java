package com.gym.crm.dao.impl;

import com.gym.crm.dao.AbstractRepositoryTest;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.dao.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = "/datasets/training-insert.sql", executionPhase = BEFORE_TEST_METHOD)
class TrainingDaoImplTest extends AbstractRepositoryTest<TrainingDao> {

    @Test
    void findAll_returnsAllTrainings() {
        List<Training> result = dao.findAll();

        assertThat(result).hasSize(3);
    }

    @Test
    void findById_existingId_returnsTrainingWithAllFields() {
        Long id = getTrainingId("Boxing Basics");

        Optional<Training> result = dao.findById(id);

        assertThat(result).isPresent();
        Training training = result.get();
        assertThat(training.getName()).isEqualTo("Boxing Basics");
        assertThat(training.getTrainingDate()).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(training.getTrainingDuration()).isEqualByComparingTo(new BigDecimal("60"));
        assertThat(training.getTrainee()).isNotNull();
        assertThat(training.getTrainee().getUser().getUsername()).isEqualTo("Abdul.Hariton");
        assertThat(training.getTrainer()).isNotNull();
        assertThat(training.getTrainer().getUser().getUsername()).isEqualTo("Mike.Tyson");
        assertThat(training.getTrainingType()).isNotNull();
        assertThat(training.getTrainingType().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Training> result = dao.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void save_newTraining_persistsAllFieldsToDB() {
        Training saved = dao.save(buildTraining(
                "Power Boxing", LocalDate.of(2024, 9, 1), new BigDecimal("75"),
                "Abdul.Hariton", "Mike.Tyson", "Boxing"));

        Optional<Training> fromDb = dao.findById(saved.getId());
        assertThat(fromDb).isPresent();

        Training training = fromDb.get();
        assertThat(training.getId()).isNotNull();
        assertThat(training.getName()).isEqualTo("Power Boxing");
        assertThat(training.getTrainingDate()).isEqualTo(LocalDate.of(2024, 9, 1));
        assertThat(training.getTrainingDuration()).isEqualByComparingTo(new BigDecimal("75"));
        assertThat(training.getTrainee().getUser().getUsername()).isEqualTo("Abdul.Hariton");
        assertThat(training.getTrainer().getUser().getUsername()).isEqualTo("Mike.Tyson");
        assertThat(training.getTrainingType().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void save_persistsToDatabase_countIncreases() {
        long before = countTrainings();

        dao.save(buildTraining(
                "Evening Yoga", LocalDate.of(2024, 10, 1), new BigDecimal("50"),
                "John.Smith", "Anna.Jones", "Yoga"));

        assertThat(countTrainings()).isEqualTo(before + 1);
    }

    @Test
    void findByTraineeCriteria_onlyUsername_returnsAllTraineeTrainings() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username("Abdul.Hariton")
                .build();

        List<Training> result = dao.findByTraineeCriteria(filter);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTrainee().getUser().getUsername()).isEqualTo("Abdul.Hariton");
        assertThat(result.get(1).getTrainee().getUser().getUsername()).isEqualTo("Abdul.Hariton");
    }

    @Test
    void findByTraineeCriteria_withFromDate_returnsFilteredTrainings() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username("Abdul.Hariton")
                .fromDate(LocalDate.of(2024, 8, 1))
                .build();

        List<Training> result = dao.findByTraineeCriteria(filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Advanced Boxing");
        assertThat(result.get(0).getTrainingDate()).isEqualTo(LocalDate.of(2024, 8, 15));
    }

    @Test
    void findByTraineeCriteria_withToDate_returnsFilteredTrainings() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username("Abdul.Hariton")
                .toDate(LocalDate.of(2024, 7, 1))
                .build();

        List<Training> result = dao.findByTraineeCriteria(filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Boxing Basics");
        assertThat(result.get(0).getTrainingDate()).isEqualTo(LocalDate.of(2024, 6, 1));
    }

    @Test
    void findByTraineeCriteria_withDateRange_returnsFilteredTrainings() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username("Abdul.Hariton")
                .fromDate(LocalDate.of(2024, 6, 1))
                .toDate(LocalDate.of(2024, 7, 1))
                .build();

        List<Training> result = dao.findByTraineeCriteria(filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Boxing Basics");
    }

    @Test
    void findByTraineeCriteria_withTrainerName_returnsFilteredTrainings() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username("Abdul.Hariton")
                .trainerFullName("Mike Tyson")
                .build();

        List<Training> result = dao.findByTraineeCriteria(filter);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTrainer().getUser().getUsername()).isEqualTo("Mike.Tyson");
    }

    @Test
    void findByTraineeCriteria_withTrainingType_returnsFilteredTrainings() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username("Abdul.Hariton")
                .trainingTypeName("Boxing")
                .build();

        List<Training> result = dao.findByTraineeCriteria(filter);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTrainingType().getTrainingTypeName()).isEqualTo("Boxing");
        assertThat(result.get(1).getTrainingType().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void findByTraineeCriteria_withAllFilters_returnsFilteredTrainings() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username("Abdul.Hariton")
                .fromDate(LocalDate.of(2024, 8, 1))
                .toDate(LocalDate.of(2024, 9, 1))
                .trainerFullName("Mike Tyson")
                .trainingTypeName("Boxing")
                .build();

        List<Training> result = dao.findByTraineeCriteria(filter);

        assertThat(result).hasSize(1);

        Training training = result.get(0);
        assertThat(training.getName()).isEqualTo("Advanced Boxing");
        assertThat(training.getTrainingDate()).isEqualTo(LocalDate.of(2024, 8, 15));
        assertThat(training.getTrainingDuration()).isEqualByComparingTo(new BigDecimal("90"));
        assertThat(training.getTrainee().getUser().getUsername()).isEqualTo("Abdul.Hariton");
        assertThat(training.getTrainer().getUser().getUsername()).isEqualTo("Mike.Tyson");
        assertThat(training.getTrainingType().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void findByTraineeCriteria_noMatchingTrainings_returnsEmpty() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username("Abdul.Hariton")
                .trainingTypeName("Yoga")
                .build();

        List<Training> result = dao.findByTraineeCriteria(filter);

        assertThat(result).isEmpty();
    }

    @Test
    void findByTrainerCriteria_onlyUsername_returnsAllTrainerTrainings() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username("Mike.Tyson")
                .build();

        List<Training> result = dao.findByTrainerCriteria(filter);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTrainer().getUser().getUsername()).isEqualTo("Mike.Tyson");
        assertThat(result.get(1).getTrainer().getUser().getUsername()).isEqualTo("Mike.Tyson");
    }

    @Test
    void findByTrainerCriteria_withFromDate_returnsFilteredTrainings() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username("Mike.Tyson")
                .fromDate(LocalDate.of(2024, 8, 1))
                .build();

        List<Training> result = dao.findByTrainerCriteria(filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Advanced Boxing");
    }

    @Test
    void findByTrainerCriteria_withToDate_returnsFilteredTrainings() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username("Mike.Tyson")
                .toDate(LocalDate.of(2024, 7, 1))
                .build();

        List<Training> result = dao.findByTrainerCriteria(filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Boxing Basics");
    }

    @Test
    void findByTrainerCriteria_withTraineeName_returnsFilteredTrainings() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username("Mike.Tyson")
                .traineeFullName("Abdul Hariton")
                .build();

        List<Training> result = dao.findByTrainerCriteria(filter);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTrainee().getUser().getUsername()).isEqualTo("Abdul.Hariton");
        assertThat(result.get(1).getTrainee().getUser().getUsername()).isEqualTo("Abdul.Hariton");
    }

    @Test
    void findByTrainerCriteria_withAllFilters_returnsFilteredTrainings() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username("Mike.Tyson")
                .fromDate(LocalDate.of(2024, 8, 1))
                .toDate(LocalDate.of(2024, 9, 1))
                .traineeFullName("Abdul Hariton")
                .build();

        List<Training> result = dao.findByTrainerCriteria(filter);

        assertThat(result).hasSize(1);

        Training training = result.get(0);
        assertThat(training.getName()).isEqualTo("Advanced Boxing");
        assertThat(training.getTrainingDate()).isEqualTo(LocalDate.of(2024, 8, 15));
        assertThat(training.getTrainingDuration()).isEqualByComparingTo(new BigDecimal("90"));
        assertThat(training.getTrainee().getUser().getUsername()).isEqualTo("Abdul.Hariton");
        assertThat(training.getTrainer().getUser().getUsername()).isEqualTo("Mike.Tyson");
        assertThat(training.getTrainingType().getTrainingTypeName()).isEqualTo("Boxing");
    }

    @Test
    void findByTrainerCriteria_noMatchingTrainings_returnsEmpty() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username("Anna.Jones")
                .fromDate(LocalDate.of(2025, 1, 1))
                .build();

        List<Training> result = dao.findByTrainerCriteria(filter);

        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("traineeFilterProvider")
    void findByTraineeCriteria_parametrized_returnsExpectedCount(TraineeTrainingSearchFilter filter, int expectedCount) {
        List<Training> result = dao.findByTraineeCriteria(filter);

        assertThat(result).hasSize(expectedCount);
    }

    static Stream<Arguments> traineeFilterProvider() {
        return Stream.of(
                Arguments.of(TraineeTrainingSearchFilter.builder()
                                .username("Abdul.Hariton")
                                .build(), 2),
                Arguments.of(TraineeTrainingSearchFilter.builder()
                                .username("Abdul.Hariton")
                                .fromDate(LocalDate.of(2024, 8, 1))
                                .build(), 1),
                Arguments.of(TraineeTrainingSearchFilter.builder()
                                .username("Abdul.Hariton")
                                .trainingTypeName("Yoga")
                                .build(), 0),
                Arguments.of(TraineeTrainingSearchFilter.builder()
                                .username("John.Smith")
                                .trainingTypeName("Yoga")
                                .build(), 1),
                Arguments.of(TraineeTrainingSearchFilter.builder()
                                .username("Abdul.Hariton")
                                .trainerFullName("Mike Tyson")
                                .build(), 2));
    }

    @ParameterizedTest
    @MethodSource("trainerFilterProvider")
    void findByTrainerCriteria_parametrized_returnsExpectedCount(TrainerTrainingSearchFilter filter, int expectedCount) {
        List<Training> result = dao.findByTrainerCriteria(filter);

        assertThat(result).hasSize(expectedCount);
    }

    static Stream<Arguments> trainerFilterProvider() {
        return Stream.of(
                Arguments.of(TrainerTrainingSearchFilter.builder()
                                .username("Mike.Tyson")
                                .build(), 2),
                Arguments.of(TrainerTrainingSearchFilter.builder()
                                .username("Anna.Jones")
                                .build(), 1),
                Arguments.of(TrainerTrainingSearchFilter.builder()
                                .username("Mike.Tyson")
                                .fromDate(LocalDate.of(2024, 8, 1))
                                .build(), 1),
                Arguments.of(TrainerTrainingSearchFilter.builder()
                                .username("Mike.Tyson")
                                .traineeFullName("Abdul Hariton")
                                .build(), 2),
                Arguments.of(TrainerTrainingSearchFilter.builder()
                                .username("Anna.Jones")
                                .fromDate(LocalDate.of(2025, 1, 1))
                                .build(), 0));
    }

    private Long getTrainingId(String trainingName) {
        try (var session = sessionFactory.openSession()) {
            return session.createQuery("SELECT t.id FROM Training t WHERE t.name = :name", Long.class)
                    .setParameter("name", trainingName)
                    .getSingleResult();
        }
    }

    private Trainee findTraineeByUsername(String username) {
        try (var session = sessionFactory.openSession()) {
            return session.createQuery("FROM Trainee t JOIN FETCH t.user WHERE t.user.username = :username", Trainee.class)
                    .setParameter("username", username)
                    .getSingleResult();
        }
    }

    private Trainer findTrainerByUsername(String username) {
        try (var session = sessionFactory.openSession()) {
            return session.createQuery("FROM Trainer t JOIN FETCH t.user JOIN FETCH t.specialization WHERE t.user.username = :username",
                            Trainer.class)
                    .setParameter("username", username)
                    .getSingleResult();
        }
    }

    private TrainingType findTrainingTypeByName(String name) {
        try (var session = sessionFactory.openSession()) {
            return session.createQuery("FROM TrainingType t WHERE t.trainingTypeName = :name", TrainingType.class)
                    .setParameter("name", name)
                    .getSingleResult();
        }
    }

    private long countTrainings() {
        try (var session = sessionFactory.openSession()) {
            return session.createQuery("SELECT COUNT(t) FROM Training t", Long.class)
                    .getSingleResult();
        }
    }

    private Training buildTraining(String name, LocalDate date, BigDecimal duration,
                                   String traineeUsername, String trainerUsername, String typeName) {
        Trainee trainee = findTraineeByUsername(traineeUsername);
        Trainer trainer = findTrainerByUsername(trainerUsername);
        TrainingType trainingType = findTrainingTypeByName(typeName);

        return Training.builder()
                .name(name)
                .trainingDate(date)
                .trainingDuration(duration)
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainingType)
                .build();
    }

    @Override
    protected Class getDaoClass() {
        return TrainingDao.class;
    }
}