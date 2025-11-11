package com.jeyah.jeyahshopapi.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeyah.jeyahshopapi.product.Product;
import com.jeyah.jeyahshopapi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderDetails {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer quantity;
    private Integer price;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore // prevent infinite loops via product relationships
    private Product product;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // prevent infinite loops via product relationships
    private User user;
}
