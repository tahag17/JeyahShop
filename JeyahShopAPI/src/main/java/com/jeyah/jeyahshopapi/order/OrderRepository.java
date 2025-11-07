package com.jeyah.jeyahshopapi.order;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jeyah.jeyahshopapi.user.User;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @EntityGraph(attributePaths = {"orderDetails", "orderDetails.product"})
    List<Order> findByUser(User user);

    @EntityGraph(attributePaths = {"orderDetails", "orderDetails.product"})
    Optional<Order> findByIdAndUser(Integer id, User user);


}
