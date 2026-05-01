package com.gym.crm.dao;

import com.gym.crm.config.TestConfig;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(TestConfig.class)
public abstract class AbstractRepositoryTest<T> {

    @Autowired
    protected SessionFactory sessionFactory;

    @Autowired
    protected ApplicationContext context;

    @Autowired
    protected T dao;

    protected abstract Class<T> getDaoClass();
}