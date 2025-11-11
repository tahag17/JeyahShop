package com.jeyah.jeyahshopapi.dashboard;

public record MostSoldCategoryDTO(
        Long id,
        String name,
        int totalSold
) {}
