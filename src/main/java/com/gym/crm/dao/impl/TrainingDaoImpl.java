package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.common.TransactionHandler;
import com.gym.crm.dao.search.TraineeTrainingQueryBuilder;
import com.gym.crm.dao.search.TrainerTrainingQueryBuilder;
import com.gym.crm.dao.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.dao.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository("trainingDao")
public class TrainingDaoImpl implements TrainingDao {

    private final TransactionHandler transactionHandler;
    private final TrainerTrainingQueryBuilder trainerQueryBuilder;
    private final TraineeTrainingQueryBuilder traineeQueryBuilder;

    @Override
    public Training save(Training training) {
        transactionHandler.executeWithinTx(session -> session.persist(training));
        log.info("Training saved: name={}", training.getName());

        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        return transactionHandler.executeReturningWithinTx(session -> Optional.ofNullable(session.get(Training.class, id)));
    }

    @Override
    public List<Training> findAll() {
        return transactionHandler.executeReturningWithinTx(session ->
                session.createQuery("""
                        FROM Training t
                        JOIN FETCH t.trainee
                        JOIN FETCH t.trainer
                        JOIN FETCH t.trainingType
                        """, Training.class)
                        .list()
        );
    }

    @Override
    public List<Training> findByTrainerCriteria(TrainerTrainingSearchFilter filter) {
        return transactionHandler.executeReturningWithinTx(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Training> query = trainerQueryBuilder.build(cb, filter);

            return session.createQuery(query).getResultList();
        });
    }

    @Override
    public List<Training> findByTraineeCriteria(TraineeTrainingSearchFilter filter) {
        return transactionHandler.executeReturningWithinTx(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Training> query = traineeQueryBuilder.build(cb, filter);

            return session.createQuery(query).getResultList();
        });
    }
}