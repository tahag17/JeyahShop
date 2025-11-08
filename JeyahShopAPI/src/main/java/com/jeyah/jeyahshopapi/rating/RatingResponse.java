package com.jeyah.jeyahshopapi.rating;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {
    private Integer productId;
    private Integer userId;
    private Integer rating;
    private double productAverageRating;
}
