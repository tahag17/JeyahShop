package com.jeyah.jeyahshopapi.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    Optional<Rating> findByUserIdAndProductId(Integer userId, Integer productId);
    List<Rating> findByProductId(Integer productId);
}
