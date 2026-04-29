package com.gym.crm.dao;

import com.gym.crm.config.TestConfig;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = {"/datasets/cleanup.sql", "/datasets/trainee-insert.sql"}, executionPhase = BEFORE_TEST_METHOD)
@SpringJUnitConfig(TestConfig.class)
public abstract class AbstractRepositoryTest {

    @Autowired
    protected SessionFactory sessionFactory;
}