package com.jeyah.jeyahshopapi.product;

import org.springframework.stereotype.Service;

@Service
public class ProductMapper {
    public Product toProduct(ProductRequest request) {
        return Product.builder()
                .id(request.id())
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .category(request.category())
                .stockQuantity(request.stockQuantity())
                .build();
    }
}
