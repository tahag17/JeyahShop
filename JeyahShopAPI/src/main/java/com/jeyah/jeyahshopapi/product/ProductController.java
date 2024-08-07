package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.TableGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
@Tag(name = "product")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Integer> addProduct(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.addProduct(request));
    }

    @GetMapping("{product-id}")
    public ResponseEntity<ProductResponse> findProductById(
            @PathVariable("product-id") Integer productId
    ) {
        return ResponseEntity.ok(productService.findProductById(productId));
    }

    @GetMapping("products")
    public ResponseEntity<PageResponse<SimpleProductResponse>> findAllProducts(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(productService.findAllProducts(page, size));
    }

    @GetMapping("products/{keyword}")
    public ResponseEntity<PageResponse<SimpleProductResponse>> findProductByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(productService.findProductsByKeyword(keyword, page, size));
    }

    @GetMapping("products/search")
    public ResponseEntity<PageResponse<SimpleProductResponse>> findProductsWithAllFilters(
            @RequestParam(required = false, defaultValue = "usb") String keyword,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false, name = "sortBy", defaultValue = "rate") String sortBy,
            @RequestParam(required = false, name = "sortDirection", defaultValue = "desc") String sortDirection,
            @RequestParam(required = false, name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(productService.searchProductsWithAllFilters(keyword, minPrice, maxPrice, tags, sortBy, sortDirection, page, size));
    }
}
