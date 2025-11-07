package com.jeyah.jeyahshopapi.cart;

import com.jeyah.jeyahshopapi.product.Product;

public record CartItemResponse (
        Integer id,
        Integer quantity,
        Integer productId,
        String productName,
        Integer price,
        String imageUrl
) {
    public static CartItemResponse from(CartItem item) {
        Product p = item.getProduct();
        return new CartItemResponse(
                item.getId(),
                item.getProductQuantity(),
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getFirstImageUrl().orElse(null)
        );
    }
}