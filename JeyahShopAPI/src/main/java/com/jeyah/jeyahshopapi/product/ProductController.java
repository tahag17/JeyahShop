package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.TableGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/public/api/products")
@RequiredArgsConstructor
@Tag(name = "product")
public class ProductController {

    private final ProductService productService;



    @GetMapping("{product-id}")
    public ResponseEntity<ProductResponse> findProductById(
            @PathVariable("product-id") Integer productId
    ) {
        System.out.println("[DEBUG] Public GET /products/" + productId + " called");
        ProductResponse product = productService.findProductById(productId);
        System.out.println("[DEBUG] Product found: " + product); // make sure ProductResponse has a proper toString
        System.out.println("[DEBUG] Image URLs: " + product.getImageUrls());
        return ResponseEntity.ok(product);
    }


    @GetMapping
    public ResponseEntity<PageResponse<SimpleProductResponse>> findAllProducts(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(productService.findAllProducts(page, size));
    }

    @GetMapping("search/{keyword}")
    public ResponseEntity<PageResponse<SimpleProductResponse>> findProductByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(productService.findProductsByKeyword(keyword, page, size));
    }

    @GetMapping("/search")
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
