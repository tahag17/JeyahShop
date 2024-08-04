package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.tag.Tag;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification implements Specification<Product> {
    private final String keyword;
    private final Integer minPrice;
    private final Integer maxPrice;
    private final List<String> tags;

    public ProductSpecification(final String keyword, final Integer minPrice, final Integer maxPrice, final List<String> tags) {
        this.keyword = keyword;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.tags = tags;
    }


    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        if (keyword != null && !keyword.isEmpty()) {
            String likePattern = "%" + keyword.toLowerCase() + "%";
            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern);
            Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern);
            Predicate categoryPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), likePattern);

            predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate, categoryPredicate));
        }

        if (pricesAreValid(minPrice, maxPrice)) {
            predicates.add(criteriaBuilder.between(root.get("price"), minPrice, maxPrice));
        }

        if (tags != null && !tags.isEmpty()) {
            Join<Product, Tag> tagJoin = root.join("tags", JoinType.LEFT);
            Predicate tagPredicate = tagJoin.get("name").in(tags);

            predicates.add(tagPredicate);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }



    private boolean pricesAreValid(Integer minPrice, Integer maxPrice) {
        return minPrice != null &&
                maxPrice != null &&
                minPrice <= maxPrice &&
                minPrice > 0;
    }
}
