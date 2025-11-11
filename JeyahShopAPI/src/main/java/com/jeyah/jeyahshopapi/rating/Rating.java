package com.jeyah.jeyahshopapi.rating;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeyah.jeyahshopapi.common.BaseEntity;
import com.jeyah.jeyahshopapi.product.Product;
import com.jeyah.jeyahshopapi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Rating extends BaseEntity {

    private Integer rating;


    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

}
