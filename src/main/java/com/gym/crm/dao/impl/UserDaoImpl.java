package com.gym.crm.dao.impl;

import com.gym.crm.dao.UserDao;
import com.gym.crm.dao.common.TransactionHandler;
import com.gym.crm.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final TransactionHandler transactionHandler;

    @Override
    public User update(User user) {
        User result = transactionHandler.executeReturningWithinTx(session -> session.merge(user));
        log.info("User updated: username={}", result.getUsername());

        return result;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("Looking up user by username={}", username);
        return transactionHandler.executeReturningWithinTx(session ->
                session.createQuery("FROM User u WHERE u.username = :username", User.class)
                        .setParameter("username", username)
                        .uniqueResultOptional());
    }
}