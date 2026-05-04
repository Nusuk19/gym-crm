package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.common.TransactionHandler;
import com.gym.crm.model.Trainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository("trainerDao")
public class TrainerDaoImpl implements TrainerDao {

    private final TransactionHandler transactionHandler;

    @Override
    public Trainer save(Trainer trainer) {
        transactionHandler.executeWithinTx(session -> session.persist(trainer));
        log.info("Trainer saved: username={}", trainer.getUser().getUsername());

        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        Trainer result = transactionHandler.executeReturningWithinTx(session -> session.merge(trainer));
        log.info("Trainer updated: username={}", result.getUser().getUsername());

        return result;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return transactionHandler.executeReturningWithinTx(session -> Optional.ofNullable(session.get(Trainer.class, id)));
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        return transactionHandler.executeReturningWithinTx(session ->
                session.createQuery(
                                "FROM Trainer t JOIN FETCH t.user JOIN FETCH t.specialization WHERE t.user.username = :username",
                                Trainer.class)
                        .setParameter("username", username)
                        .uniqueResultOptional()
        );
    }

    @Override
    public List<Trainer> findAll() {
        return transactionHandler.executeReturningWithinTx(session ->
                session.createQuery("SELECT DISTINCT t FROM Trainer t JOIN FETCH t.user", Trainer.class).list()
        );
    }

    @Override
    public List<Trainer> findAllNotAssignedToTrainee(String traineeUsername) {
        return transactionHandler.executeReturningWithinTx(session ->
                session.createQuery(
                                "SELECT t FROM Trainer t " +
                                        "LEFT JOIN t.trainees trn WITH trn.user.username = :username " +
                                        "WHERE trn.id IS NULL",
                                Trainer.class)
                        .setParameter("username", traineeUsername)
                        .getResultList()
        );
    }
}
