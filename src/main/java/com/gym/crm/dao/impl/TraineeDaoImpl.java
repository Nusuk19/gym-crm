package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Repository
public class TraineeDaoImpl implements TraineeDao {

    private final SessionFactory sessionFactory;

    @Override
    public Trainee save(Trainee trainee) {
        sessionFactory.getCurrentSession().persist(trainee);
        log.info("Trainee saved: username={}", trainee.getUser().getUsername());

        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Trainee merged = sessionFactory.getCurrentSession().merge(trainee);
        log.info("Trainee updated: username={}", merged.getUser().getUsername());

        return merged;
    }

    @Override
    public void deleteByUsername(String username) {
        findByUsername(username).ifPresent(trainee -> {
            sessionFactory.getCurrentSession().remove(trainee);
            log.info("Trainee deleted: username={}", username);
        });
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        Trainee trainee = sessionFactory.getCurrentSession().get(Trainee.class, id);

        return Optional.ofNullable(trainee);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Trainee t WHERE t.user.username = :username", Trainee.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    @Override
    public List<Trainee> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Trainee", Trainee.class)
                .list();
    }
}
