package com.jeyah.jeyahshopapi.cart;

import com.jeyah.jeyahshopapi.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    @EntityGraph(attributePaths = {"cartItems", "cartItems.product"})
    Optional<Cart> findByUser(User user);



}
