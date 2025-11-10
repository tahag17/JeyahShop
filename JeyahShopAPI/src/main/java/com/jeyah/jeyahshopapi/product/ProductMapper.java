package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.category.Category;
import com.jeyah.jeyahshopapi.category.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductMapper {

    @Value("${r2.public-base-url}")
    private String r2BaseUrl;
    private final CategoryRepository categoryRepository;

    public Product toProduct(ProductRequest request) {

        List<ProductImage> productImages = request.imageUrls().stream()
                .map(url -> new ProductImage(null, url, null))
                .collect(Collectors.toList());

        // Fetch the Category entity
        Category category = categoryRepository.findById(request.categoryId()) // assuming ProductRequest has categoryId
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID " + request.categoryId()));


        Product product = Product.builder()
                .id(request.id())
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .category(category)
                .stockQuantity(request.stockQuantity())
                .productImages(productImages)
                .build();

        productImages.forEach(image -> image.setProduct(product));

        return product;
    }

    public Product toProductWithoutImages(ProductRequest request) {
        // Fetch the Category entity
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID " + request.categoryId()));

        return Product.builder()
                .id(request.id())
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .category(category)  // set the entity instead of string
                .stockQuantity(request.stockQuantity())
                .build();
    }


    public ProductResponse toProductResponse(Product product) {
        List<String> imageUrls = product.getProductImages().stream()
                .map(img -> r2BaseUrl + img.getUrl()) // prepend public R2 URL
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .stockQuantity(product.getStockQuantity())
                .rate(product.getRate())
                .imageUrls(imageUrls)
                .available(product.getStockQuantity() != 0)
                .build();
    }
    public SimpleProductResponse toSimpleProductResponse(Product product) {
        Optional<String> firstImage = product.getFirstImageUrl();
        String imageUrl = firstImage.orElse(null);

        return SimpleProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .rate(product.getRate())
                .available(product.getStockQuantity() != 0)
                .imageUrl(Optional.ofNullable(imageUrl))
                .build();
    }

}
