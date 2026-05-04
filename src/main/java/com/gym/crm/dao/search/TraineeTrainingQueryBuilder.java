package com.gym.crm.dao.search;

import com.gym.crm.dao.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.model.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TraineeTrainingQueryBuilder extends TrainingQueryBuilder<TraineeTrainingSearchFilter> {

    @Override
    protected void addSpecificPredicates(CriteriaBuilder cb, Root<Training> root, TraineeTrainingSearchFilter criteria,
                                         List<Predicate> predicates, Map<String, Join<?, ?>> joinCache) {
        addTrainerFullNamePredicate(cb, root, criteria, predicates, joinCache);
        addTrainingTypePredicate(cb, root, criteria, predicates);
    }

    @Override
    protected Predicate getUsernamePredicate(CriteriaBuilder cb, Root<Training> root, String username,
                                             Map<String, Join<?, ?>> joinCache) {
        Join<?, ?> userJoin = resolveJoinPath(root, TrainingAttribute.TRAINEE_USER, joinCache);

        return cb.equal(userJoin.get(TrainingAttribute.USERNAME), username);
    }

    private void addTrainerFullNamePredicate(CriteriaBuilder cb, Root<Training> root, TraineeTrainingSearchFilter criteria,
                                             List<Predicate> predicates, Map<String, Join<?, ?>> joinCache) {
        addFullNameLikePredicate(cb, root, predicates, criteria.getTrainerFullName(), TrainingAttribute.TRAINER_USER, joinCache);
    }

    private void addTrainingTypePredicate(CriteriaBuilder cb, Root<Training> root,
                                          TraineeTrainingSearchFilter criteria, List<Predicate> predicates) {
        Optional.ofNullable(criteria.getTrainingTypeName())
                .filter(name -> !name.isBlank())
                .ifPresent(typeName -> predicates.add(
                        cb.equal(root.get("trainingType").get(TrainingAttribute.TRAINING_TYPE_NAME), typeName)));
    }
}