package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {

    @Id
    private Integer id;
    private String name;
    private  Integer price;
    private String description;
    private String category;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
    private Integer stockQuantity;
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Integer postedBy;
    @LastModifiedBy
    @Column(insertable = false)
    private Integer lastModifiedBy;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
