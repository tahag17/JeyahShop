package com.jeyah.jeyahshopapi.product;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.TableGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
@Tag(name = "product")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Integer> addProduct(
            @Valid @RequestBody ProductRequest request,
            Authentication connectedUser
    ) {
    return ResponseEntity.ok(productService.addProduct(request, connectedUser));
    }
}
