package com.gym.crm.transaction;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Slf4j
public final class TransactionScope implements AutoCloseable {

    private final Session session;
    private final Transaction transaction;
    private final boolean ownsTransaction;

    private boolean isTransactionFailed = false;

    private TransactionScope(Session session, Transaction transaction, boolean ownsTransaction) {
        this.session = session;
        this.transaction = transaction;
        this.ownsTransaction = ownsTransaction;
    }

    public static TransactionScope open(SessionFactory factory, boolean readOnly) {
        Session session = factory.getCurrentSession();
        boolean alreadyActive = session.getTransaction() != null && session.getTransaction().isActive();

        if (alreadyActive) {
            log.debug("Joining existing transaction");

            return new TransactionScope(session, session.getTransaction(), false);
        }

        Transaction tx = session.beginTransaction();
        if (readOnly) {
            session.setDefaultReadOnly(true);
        }

        log.debug("Started new transaction");

        return new TransactionScope(session, tx, true);
    }

    public Session session() {
        return session;
    }

    public void markFailed() {
        this.isTransactionFailed = true;
    }

    @Override
    public void close() {
        if (!ownsTransaction) {
            return;
        }

        if (isTransactionFailed && transaction.isActive()) {
                transaction.rollback();
                log.debug("Transaction rolled back");
                return;
        }

        transaction.commit();
        log.debug("Transaction committed");
    }
}