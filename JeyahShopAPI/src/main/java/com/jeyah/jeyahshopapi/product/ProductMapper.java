package com.jeyah.jeyahshopapi.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductMapper {

    @Value("${r2.public-base-url}")
    private String r2BaseUrl;

    public Product toProduct(ProductRequest request) {

        List<ProductImage> productImages = request.imageUrls().stream()
                .map(url -> new ProductImage(null, url, null))
                .collect(Collectors.toList());

        Product product = Product.builder()
                .id(request.id())
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .category(request.category())
                .stockQuantity(request.stockQuantity())
                .productImages(productImages)
                .build();

        productImages.forEach(image -> image.setProduct(product));

        return product;
    }

    public Product toProductWithoutImages(ProductRequest request) {

        return Product.builder()
                .id(request.id())
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .category(request.category())
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
                .category(product.getCategory())
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
