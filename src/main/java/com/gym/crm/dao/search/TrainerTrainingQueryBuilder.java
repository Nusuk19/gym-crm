package com.gym.crm.dao.search;

import com.gym.crm.dao.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TrainerTrainingQueryBuilder extends TrainingQueryBuilder<TrainerTrainingSearchFilter> {

    @Override
    protected void addSpecificPredicates(CriteriaBuilder cb, Root<Training> root, TrainerTrainingSearchFilter criteria,
                                         List<Predicate> predicates, Map<String, Join<?, ?>> joinCache) {
        addTraineeFullNamePredicate(cb, root, criteria, predicates, joinCache);
    }

    @Override
    protected Predicate getUsernamePredicate(CriteriaBuilder cb, Root<Training> root,
                                             String username, Map<String, Join<?, ?>> joinCache) {
        Join<?, ?> userJoin = resolveJoinPath(root, TrainingAttribute.TRAINER_USER, joinCache);

        return cb.equal(userJoin.get(TrainingAttribute.USERNAME), username);
    }

    private void addTraineeFullNamePredicate(CriteriaBuilder cb, Root<Training> root, TrainerTrainingSearchFilter criteria,
                                             List<Predicate> predicates, Map<String, Join<?, ?>> joinCache) {
        addFullNameLikePredicate(cb, root, predicates, criteria.getTraineeFullName(), TrainingAttribute.TRAINEE_USER, joinCache);
    }
}