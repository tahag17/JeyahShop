package com.jeyah.jeyahshopapi.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeyah.jeyahshopapi.cart.CartItem;
import com.jeyah.jeyahshopapi.category.Category;
import com.jeyah.jeyahshopapi.common.BaseEntity;
import com.jeyah.jeyahshopapi.rating.Rating;
import com.jeyah.jeyahshopapi.tag.Tag;
import com.jeyah.jeyahshopapi.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.prefs.BackingStoreException;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product extends BaseEntity {


    private String name;
    private Integer price;
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore // prevent Category -> Product -> Category recursion
    private Category category;
    private Integer stockQuantity;



    @ManyToMany
    private List<Tag> tags;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages;

    @Transient
    public Optional<String> getFirstImageUrl() {
        return productImages != null && !productImages.isEmpty()
                ? Optional.of(productImages.get(0).getUrl())
                : Optional.empty();
    }

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<Rating> ratings;

    @ManyToOne
    private User user;

    @Transient
    public double getRate() {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }
        double rate = this.ratings.stream()
                .mapToDouble(Rating::getRating)
                .average()
                .orElse(0.0);
        double roundedRate = Math.round(rate * 10.0) / 10.0;
        return roundedRate;
    }

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<CartItem> cartItems;
}
