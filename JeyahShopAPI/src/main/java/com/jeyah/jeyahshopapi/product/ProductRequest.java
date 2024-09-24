package com.jeyah.jeyahshopapi.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

import java.util.List;

public record ProductRequest(
        // id == 0 -> we want to create a new book
        // id != 0 -> we want to update an existing book

        Integer id,

        @NotNull(message = "100")
        @NotEmpty(message = "100")
        String name,

        @NotNull(message = "101")
        @Min(value = 0, message = "101")
        Integer price,

        @NotNull(message = "102")
        @NotEmpty(message = "102")
        String description,

        @NotNull(message = "103")
        @NotEmpty(message = "103")
        String category,

        @NotNull(message = "104")
        @Min(value = 0, message = "104")
        Integer stockQuantity,

        List<String> imageUrls

) {

}
