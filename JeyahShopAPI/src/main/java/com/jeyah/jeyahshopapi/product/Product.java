package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.cart.CartItem;
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
    private  Integer price;
    private String description;
    private String category;
    private Integer stockQuantity;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    private List<Tag> tags;

    @OneToMany
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;
}
