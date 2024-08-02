package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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
}
