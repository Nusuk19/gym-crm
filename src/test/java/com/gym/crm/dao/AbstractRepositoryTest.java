package com.gym.crm.dao;

import com.gym.crm.config.TestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(TestConfig.class)
public abstract class AbstractRepositoryTest {

    @Autowired
    protected SessionFactory sessionFactory;

    @BeforeEach
    void beginTransaction() {
        sessionFactory.getCurrentSession().beginTransaction();
    }

    @AfterEach
    void rollbackTransaction() {
        sessionFactory.getCurrentSession().getTransaction().rollback();
    }

    protected void flushAndClear() {
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
    }
}