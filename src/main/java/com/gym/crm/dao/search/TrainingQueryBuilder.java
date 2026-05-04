package com.gym.crm.dao.search;

import com.gym.crm.dao.search.filters.TrainingSearchFilter;
import com.gym.crm.entity.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class TrainingQueryBuilder<F extends TrainingSearchFilter> {

    public CriteriaQuery<Training> build(CriteriaBuilder cb, F criteria) {
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);
        List<Predicate> predicates = new ArrayList<>();
        Map<String, Join<?, ?>> joinCache = new HashMap<>();

        addUsernamePredicate(cb, root, criteria, predicates, joinCache);
        addDateRange(cb, root, criteria, predicates);
        addSpecificPredicates(cb, root, criteria, predicates, joinCache);

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

        return query;
    }

    protected abstract void addSpecificPredicates(CriteriaBuilder cb, Root<Training> root, F criteria,
                                                  List<Predicate> predicates, Map<String, Join<?, ?>> joinCache);

    protected abstract Predicate getUsernamePredicate(CriteriaBuilder cb, Root<Training> root,
                                                      String username, Map<String, Join<?, ?>> joinCache);

    protected void addUsernamePredicate(CriteriaBuilder cb, Root<Training> root, F criteria,
                                        List<Predicate> predicates, Map<String, Join<?, ?>> joinCache) {
        Optional.ofNullable(criteria.getUsername())
                .filter(name -> !name.isBlank())
                .ifPresent(username -> predicates.add(getUsernamePredicate(cb, root, username, joinCache)));
    }

    protected void addDateRange(CriteriaBuilder cb, Root<Training> root, F criteria, List<Predicate> predicates) {
        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(from -> predicates.add(cb.greaterThanOrEqualTo(root.get(TrainingPaths.TRAINING_DATE), from)));

        Optional.ofNullable(criteria.getToDate())
                .ifPresent(to -> predicates.add(cb.lessThanOrEqualTo(root.get(TrainingPaths.TRAINING_DATE), to)));
    }

    protected void addFullNameLikePredicate(CriteriaBuilder cb, Root<Training> root, List<Predicate> predicates,
                                            String fullName, String userPathPrefix, Map<String, Join<?, ?>> joinCache) {
        Optional.ofNullable(fullName)
                .filter(name -> !name.isBlank())
                .ifPresent(name -> {
                    Join<?, ?> userJoin = resolveJoinPath(root, userPathPrefix, joinCache);

                    Expression<String> fullNameExpr = cb.concat(
                            cb.concat(userJoin.get(TrainingPaths.FIRST_NAME), " "),
                            userJoin.get(TrainingPaths.LAST_NAME)
                    );

                    predicates.add(cb.like(cb.lower(fullNameExpr), "%" + name.toLowerCase() + "%"));
                });
    }

    protected Join<?, ?> resolveJoinPath(Root<Training> root, String path, Map<String, Join<?, ?>> joinCache) {
        String[] parts = path.split("\\.");

        Join<?, ?> current = root.join(parts[0], JoinType.LEFT);
        joinCache.putIfAbsent(parts[0], current);

        for (int i = 1; i < parts.length; i++) {
            String subPath = String.join(".", Arrays.copyOfRange(parts, 0, i + 1));

            final Join<?, ?> parent = current;
            final String segment = parts[i];

            current = joinCache.computeIfAbsent(subPath, ignored -> parent.join(segment, JoinType.LEFT));
        }

        return current;
    }
}