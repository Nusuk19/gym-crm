package com.gym.crm.dao.common;

import com.gym.crm.transaction.TransactionScope;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TransactionHandler {

    private final SessionFactory sessionFactory;

    public void executeWithinTx(Consumer<Session> sessionConsumer) {
        executeReturningWithinTx(session -> {
            sessionConsumer.accept(session);

            return null;
        });
    }

    public <T> T executeReturningWithinTx(Function<Session, T> sessionFunction) {
        try (TransactionScope scope = TransactionScope.open(sessionFactory, false)) {

            try {
                return sessionFunction.apply(scope.session());
            } catch (RuntimeException e) {
                scope.markFailed();

                throw e;
            }
        }
    }
}