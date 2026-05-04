package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.dao.common.TransactionHandler;
import com.gym.crm.entity.Trainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository("traineeDao")
public class TraineeDaoImpl implements TraineeDao {

    private final TransactionHandler transactionHandler;

    @Override
    public Trainee save(Trainee trainee) {
        transactionHandler.executeWithinTx(session -> session.persist(trainee));
        log.info("Trainee saved: username={}", trainee.getUser().getUsername());

        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Trainee result = transactionHandler.executeReturningWithinTx(session -> session.merge(trainee));
        log.info("Trainee updated: username={}", result.getUser().getUsername());

        return result;
    }

    @Override
    public void updateTrainers(String traineeUsername, List<String> trainerUsernames) {
        transactionHandler.executeWithinTx(session -> {
            Trainee trainee = session.createQuery(
                            "SELECT t FROM Trainee t JOIN FETCH t.trainers WHERE t.user.username = :username",
                            Trainee.class)
                    .setParameter("username", traineeUsername)
                    .uniqueResult();

            if (trainee == null) {
                log.warn("Trainee not found for trainers update: username={}", traineeUsername);
                return;
            }

            List<Trainer> newTrainers = session.createQuery(
                            "SELECT t FROM Trainer t WHERE t.user.username IN :usernames", Trainer.class)
                    .setParameter("usernames", trainerUsernames)
                    .getResultList();

            trainee.getTrainers().clear();
            trainee.getTrainers().addAll(newTrainers);

            log.info("Updated trainers list for trainee: username={}, trainers={}",
                    traineeUsername, trainerUsernames);
        });
    }

    @Override
    public void deleteById(Long id) {
        transactionHandler.executeWithinTx(session -> {
            Trainee trainee = session.get(Trainee.class, id);
            if (trainee != null) {
                session.remove(trainee);
                log.info("Trainee deleted: id={}", id);
            } else {
                log.warn("Trainee not found for deletion: id={}", id);
            }
        });
    }

    @Override
    public void deleteByUsername(String username) {
        transactionHandler.executeWithinTx(session -> {
            Trainee trainee = session.createQuery(
                            "FROM Trainee t JOIN FETCH t.user WHERE t.user.username = :username",
                            Trainee.class)
                    .setParameter("username", username)
                    .uniqueResult();

            if (trainee != null) {
                session.remove(trainee);
                log.info("Trainee deleted: username={}", username);
            }
        });
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return transactionHandler.executeReturningWithinTx(session -> Optional.ofNullable(session.get(Trainee.class, id)));
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return transactionHandler.executeReturningWithinTx(session ->
                session.createQuery(
                                "FROM Trainee t JOIN FETCH t.user LEFT JOIN FETCH t.trainers WHERE t.user.username = :username",
                                Trainee.class)
                        .setParameter("username", username)
                        .uniqueResultOptional()
        );
    }

    @Override
    public List<Trainee> findAll() {
        return transactionHandler.executeReturningWithinTx(session ->
                session.createQuery("SELECT DISTINCT t FROM Trainee t JOIN FETCH t.user", Trainee.class).list()
        );
    }
}