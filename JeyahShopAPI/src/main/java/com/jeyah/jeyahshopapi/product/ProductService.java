package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.common.PageResponse;
import com.jeyah.jeyahshopapi.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Integer addProduct(ProductRequest request) {
        Product product = productMapper.toProduct(request);
        return productRepository.save(product).getId();
    }

    public ProductResponse findProductById(Integer productId) {
        return productRepository.findById(productId)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("No product with ID '" + productId + "' found."));
    }

    public PageResponse<SimpleProductResponse> findAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Product> products = productRepository.findAll(pageable);
        List<SimpleProductResponse> simpleProductResponse = products.stream()
                .map(ProductMapper::toSimpleProductResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                simpleProductResponse,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
        );
    }

    public PageResponse<SimpleProductResponse> findProductsByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Product> products = productRepository.findProductsByKeyword(keyword, pageable);
        List<SimpleProductResponse> simpleProductResponse = products.stream()
                .map(ProductMapper::toSimpleProductResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                simpleProductResponse,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
        );
    }

    public PageResponse<SimpleProductResponse> searchProductsWithAllFilters(
            String keyword,
            Integer minPrice,
            Integer maxPrice,
            List<String> tags,
            String sortBy,
            String sortDirection,
            int page,
            int size
    ) {
        Sort.Direction direction = "asc".equals(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Specification<Product> spec = new ProductSpecification(keyword, minPrice, maxPrice, tags);
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        //sorting products based on rating
        List<Product> sortedProducts = productPage.getContent().stream()
                .sorted(Comparator.comparingDouble(Product::getRate).reversed())
                .collect(Collectors.toList());

        List<SimpleProductResponse> simpleProductResponses = sortedProducts.stream()
                .map(ProductMapper::toSimpleProductResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                simpleProductResponses,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast()
        );
    }

}
