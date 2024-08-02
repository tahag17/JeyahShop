package com.jeyah.jeyahshopapi.tag;


import com.jeyah.jeyahshopapi.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tag {

    @Id
    private Integer id;
    private String name;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<Product> productList;
}
