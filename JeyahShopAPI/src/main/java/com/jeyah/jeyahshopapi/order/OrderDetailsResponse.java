package com.jeyah.jeyahshopapi.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsResponse {
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Integer price; // price per item
    private Integer totalPrice; // price * quantity
}
