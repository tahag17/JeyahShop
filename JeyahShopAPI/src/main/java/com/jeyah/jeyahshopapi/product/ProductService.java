package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.common.PageResponse;
import com.jeyah.jeyahshopapi.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Integer addProduct(ProductRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Product product = productMapper.toProduct(request);
        product.setUser(user);
        return productRepository.save(product).getId();
    }

    public ProductResponse findProductById(Integer productId) {
        return productRepository.findById(productId)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("No product with ID '"+productId+"' found."));
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

}
