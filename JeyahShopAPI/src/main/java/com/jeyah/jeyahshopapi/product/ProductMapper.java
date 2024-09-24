package com.jeyah.jeyahshopapi.product;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductMapper {
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

    public ProductResponse toProductResponse(Product product) {

        List<String> imageUrls = product.getProductImages().stream()
                .map(ProductImage::getUrl)
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

    public static SimpleProductResponse toSimpleProductResponse(Product product) {
        return SimpleProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .rate(product.getRate())
                .available(product.getStockQuantity() != 0)
                .imageUrl(product.getFirstImageUrl())
                .build();
    }
}
