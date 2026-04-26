package com.gym.crm.config;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class, HibernateConfig.class})
class HibernateConfigTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    void sessionFactory_whenContextLoaded_isNotNull() {
        assertNotNull(sessionFactory);
    }

    @Test
    void sessionFactory_whenCreated_isNotClosed() {
        assertFalse(sessionFactory.isClosed());
    }

    @Test
    void session_whenOpened_isOpen() {
        try (Session session = sessionFactory.openSession()) {
            assertTrue(session.isOpen());
        }
    }

    @Test
    void session_whenOpened_isConnectedToDatabase() {
        try (Session session = sessionFactory.openSession()) {
            assertTrue(session.isConnected());
        }
    }

    @Test
    void session_canLoadMetadata() {
        try (Session session = sessionFactory.openSession()) {
            assertNotNull(session.getMetamodel().entity(User.class));
            assertNotNull(session.getMetamodel().entity(Trainee.class));
            assertNotNull(session.getMetamodel().entity(Trainer.class));
            assertNotNull(session.getMetamodel().entity(Training.class));
            assertNotNull(session.getMetamodel().entity(TrainingType.class));
        }
    }
}