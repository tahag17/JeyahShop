package com.jeyah.jeyahshopapi.cart;

import com.jeyah.jeyahshopapi.product.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {


    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    List<CartItem> findByCart(Cart cart);

    @EntityGraph(attributePaths = {"cart", "product"})
    @Query("SELECT ci FROM CartItem ci WHERE ci.id = :id")
    Optional<CartItem> findByIdWithCart(@Param("id") Integer id);

}
