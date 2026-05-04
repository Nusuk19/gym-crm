package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.dao.common.TransactionHandler;
import com.gym.crm.model.TrainingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    private final TransactionHandler transactionHandler;

    @Override
    public Optional<TrainingType> findById(Long id) {
        return transactionHandler.executeReturningWithinTx(session -> Optional.ofNullable(session.get(TrainingType.class, id)));
    }

    @Override
    public Optional<TrainingType> findByTrainingTypeName(String typeName) {
        return transactionHandler.executeReturningWithinTx(session ->
                session.createQuery(
                                "FROM TrainingType t WHERE t.trainingTypeName = :name",
                                TrainingType.class)
                        .setParameter("name", typeName)
                        .uniqueResultOptional()
        );
    }

    @Override
    public List<TrainingType> findAll() {
        return transactionHandler.executeReturningWithinTx(session -> session.createQuery("FROM TrainingType", TrainingType.class).list());
    }
}
