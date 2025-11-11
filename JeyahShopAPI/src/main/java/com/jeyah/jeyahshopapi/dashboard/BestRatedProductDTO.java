package com.jeyah.jeyahshopapi.dashboard;

public record BestRatedProductDTO(
        Long id,
        String name,
        String imageUrl,
        double rate
) {}
