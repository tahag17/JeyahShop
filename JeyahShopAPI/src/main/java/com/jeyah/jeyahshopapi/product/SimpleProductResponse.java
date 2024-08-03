package com.jeyah.jeyahshopapi.product;

import lombok.*;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleProductResponse {
    private Integer id;
    private String name;
    private Integer price;
    private double rate;
    private boolean available;
    // todo probably should find another way to validate a product
    private Optional<String> imageUrl;
}
