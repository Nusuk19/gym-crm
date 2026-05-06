package com.gym.crm.aspect;

import com.gym.crm.annotation.PersistenceTx;
import com.gym.crm.transaction.TransactionScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TransactionAspect {

    private final SessionFactory sessionFactory;

    @Around("@annotation(persistenceTx)")
    public Object handleTransaction(ProceedingJoinPoint joinPoint, PersistenceTx persistenceTx) throws Throwable {
        try (TransactionScope scope = TransactionScope.open(sessionFactory, persistenceTx.readOnly())) {

            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                scope.markFailed();
                log.warn("Transaction rolled back for {}: {}", joinPoint.getSignature().toShortString(), e.getMessage());
                
                throw e;
            }
        }
    }
}