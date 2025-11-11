package com.jeyah.jeyahshopapi.home;

import com.jeyah.jeyahshopapi.category.Category;
import com.jeyah.jeyahshopapi.category.CategoryRepository;
import com.jeyah.jeyahshopapi.product.Product;
import com.jeyah.jeyahshopapi.product.ProductMapper;
import com.jeyah.jeyahshopapi.product.ProductRepository;
import com.jeyah.jeyahshopapi.product.SimpleProductResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public/api/home")
@RequiredArgsConstructor
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHomeContent() {
        log.info("🟢 [HomeController] /public/api/home called");

        Map<String, Object> response = new HashMap<>();

        // 1️⃣ Top rated products
        List<Product> allProducts = productRepository.findAll();
        log.info("📦 Found {} total products", allProducts.size());

        List<Product> topRated = allProducts.stream()
                .sorted(Comparator.comparingDouble(Product::getRate).reversed())
                .limit(10)
                .toList();
        log.info("⭐ Selected {} top-rated products", topRated.size());

        response.put("topRated", topRated.stream()
                .map(productMapper::toSimpleProductResponse)
                .toList());

        // 2️⃣ Newest products
        List<Product> newest = productRepository.findAll(
                PageRequest.of(0, 10, Sort.by("createdDate").descending())
        ).getContent();
        log.info("🆕 Selected {} latest products", newest.size());
        response.put("latest", newest.stream()
                .map(productMapper::toSimpleProductResponse)
                .toList());

        // 3️⃣ Random 10 per category
        List<Category> categories = categoryRepository.findAll();
        log.info("📚 Found {} categories", categories.size());

        Map<String, List<SimpleProductResponse>> categorySamples = new HashMap<>();
        for (Category cat : categories) {
            List<Product> randomProducts = cat.getProducts().stream()
                    .sorted((a, b) -> Math.random() < 0.5 ? -1 : 1)
                    .limit(10)
                    .toList();

            log.info("🎯 Category '{}' → {} products", cat.getName(), randomProducts.size());

            categorySamples.put(
                    cat.getName(),
                    randomProducts.stream().map(productMapper::toSimpleProductResponse).toList()
            );
        }

        response.put("categorySamples", categorySamples);
        log.info("✅ Home data prepared successfully");

        return ResponseEntity.ok(response);
    }
}

