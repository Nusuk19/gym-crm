package com.gym.crm.dao.impl;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeDaoImplTest {

    private static final Long EXISTING_ID = 1L;
    private static final Long NON_EXISTING_ID = 99L;

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Query<Trainee> query;
    @Mock
    private MutationQuery mutationQuery;
    @InjectMocks
    private TraineeDaoImpl traineeDao;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = createTrainee();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void save_shouldPersistAndReturnEntity() {
        Trainee result = traineeDao.save(trainee);

        verify(session).persist(trainee);
        verify(sessionFactory).getCurrentSession();
        assertThat(result).isSameAs(trainee);
    }

    @Test
    void update_shouldMergeAndReturnMergedEntity() {
        when(session.merge(trainee)).thenReturn(trainee);

        Trainee result = traineeDao.update(trainee);

        verify(session).merge(trainee);
        assertThat(result).isSameAs(trainee);
    }

    @Test
    void deleteByUsername_shouldExecuteDeleteQuery() {
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter(eq("username"), eq("Abdul.Hariton"))).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(trainee));

        traineeDao.deleteByUsername("Abdul.Hariton");

        verify(session).remove(trainee);
    }

    @Test
    void findById_whenExists_shouldReturnTrainee() {
        when(session.get(Trainee.class, EXISTING_ID)).thenReturn(trainee);

        Optional<Trainee> result = traineeDao.findById(EXISTING_ID);

        assertThat(result).isPresent().contains(trainee);
        verify(session).get(Trainee.class, EXISTING_ID);
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {
        when(session.get(Trainee.class, NON_EXISTING_ID)).thenReturn(null);

        Optional<Trainee> result = traineeDao.findById(NON_EXISTING_ID);

        assertThat(result).isEmpty();
        verify(session).get(Trainee.class, NON_EXISTING_ID);
    }

    @Test
    void findByUsername_whenExists_shouldReturnEntity() {
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter(eq("username"), eq("Abdul.Hariton"))).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = traineeDao.findByUsername("Abdul.Hariton");

        assertThat(result).isPresent().contains(trainee);
        verify(session).createQuery(anyString(), eq(Trainee.class));
        verify(query).setParameter("username", "Abdul.Hariton");
    }

    @Test
    void findByUsername_whenNotExists_shouldReturnEmpty() {
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeDao.findByUsername("unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnList() {
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.list()).thenReturn(List.of(trainee));

        List<Trainee> result = traineeDao.findAll();

        assertThat(result).hasSize(1).containsExactly(trainee);
        verify(session).createQuery(anyString(), eq(Trainee.class));
        verify(query).list();
    }

    private Trainee createTrainee() {
        User user = User.builder()
                .username("Abdul.Hariton")
                .firstName("Abdul")
                .lastName("Hariton")
                .password("hashedPassword")
                .isActive(true)
                .build();

        return Trainee.builder()
                .user(user)
                .build();
    }
}