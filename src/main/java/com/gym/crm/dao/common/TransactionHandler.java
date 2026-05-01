package com.gym.crm.dao.common;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TransactionHandler {

    private final SessionFactory sessionFactory;

    public void executeWithinTx(Consumer<Session> sessionConsumer) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            sessionConsumer.accept(session);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public <T> T executeReturningWithinTx(Function<Session, T> sessionFunction) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            T result = sessionFunction.apply(session);
            tx.commit();
            return result;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}