package com.interview.specification;

import com.interview.model.User;
import com.interview.model.User_;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

@AllArgsConstructor
public class UserSpecification implements Specification<User> {

    private final User filter;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate predicate = builder.conjunction();
        query.orderBy(builder.desc(root.get(User_.id)));
        predicate.getExpressions().add(builder.equal(root.get("active"),true));

        return predicate;
    }
}
