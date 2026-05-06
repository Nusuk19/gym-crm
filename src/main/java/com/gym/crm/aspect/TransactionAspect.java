package com.gym.crm.aspect;

import com.gym.crm.annotation.PersistenceTx;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TransactionAspect {

    private final SessionFactory sessionFactory;

    @Around("@annotation(persistenceTx)")
    public Object handleTransaction(ProceedingJoinPoint joinPoint, PersistenceTx persistenceTx) throws Throwable {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            if (persistenceTx.readOnly()) {
                session.setDefaultReadOnly(true);
            }

            Object result = joinPoint.proceed();

            transaction.commit();
            log.debug("Transaction committed for: {}", joinPoint.getSignature().toShortString());

            return result;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                log.warn("Transaction rolled back for: {} due to: {}", joinPoint.getSignature().toShortString(), e.getMessage());
            }

            throw e;
        }
    }
}