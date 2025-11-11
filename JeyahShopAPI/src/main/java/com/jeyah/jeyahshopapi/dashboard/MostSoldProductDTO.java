package com.jeyah.jeyahshopapi.dashboard;

public record MostSoldProductDTO(
        Long id,
        String name,
        String imageUrl,
        int totalSold,
        double rate
) {}
