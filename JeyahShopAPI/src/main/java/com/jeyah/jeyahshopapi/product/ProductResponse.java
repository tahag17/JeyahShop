package com.jeyah.jeyahshopapi.product;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private Integer id;
    private String name;
    private Integer price;
    private String description;
    private Integer categoryId;
    private Integer stockQuantity;
    private List<String> imageUrls;
    private double rate;
    private boolean available;


}
