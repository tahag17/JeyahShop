package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.category.Category;
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

    public ProductSpecification(String keyword, Integer minPrice, Integer maxPrice) {
        this.keyword = keyword;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            String likePattern = "%" + keyword.toLowerCase() + "%";
            Predicate namePredicate = cb.like(cb.lower(root.get("name")), likePattern);
            Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), likePattern);
            Join<Product, Category> categoryJoin = root.join("category", JoinType.LEFT);
            Predicate categoryPredicate = cb.like(cb.lower(categoryJoin.get("name")), likePattern);

            predicates.add(cb.or(namePredicate, descriptionPredicate, categoryPredicate));
        }

        if (minPrice != null && maxPrice != null && minPrice <= maxPrice && minPrice > 0) {
            predicates.add(cb.between(root.get("price"), minPrice, maxPrice));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}

