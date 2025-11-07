package com.jeyah.jeyahshopapi.cart;

import java.util.List;

public record CartResponse (
        Integer id,
        Integer totalPrice,
        List<CartItemResponse> items
) {
    public static CartResponse from(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getTotalPrice(),
                cart.getCartItems().stream().map(CartItemResponse::from).toList()
        );
    }
}