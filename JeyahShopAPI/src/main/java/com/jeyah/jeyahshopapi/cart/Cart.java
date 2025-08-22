package com.jeyah.jeyahshopapi.cart;


import com.jeyah.jeyahshopapi.common.BaseEntity;
import com.jeyah.jeyahshopapi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.prefs.BackingStoreException;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart extends BaseEntity {

    private Integer totalPrice;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems;
}
