package com.jeyah.jeyahshopapi.order;

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
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
}
