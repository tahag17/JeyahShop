package com.jeyah.jeyahshopapi.dashboard;

import com.jeyah.jeyahshopapi.category.Category;
import com.jeyah.jeyahshopapi.category.CategoryRepository;
import com.jeyah.jeyahshopapi.order.Order;
import com.jeyah.jeyahshopapi.order.OrderDetails;
import com.jeyah.jeyahshopapi.order.OrderRepository;
import com.jeyah.jeyahshopapi.product.Product;
import com.jeyah.jeyahshopapi.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Endpoint that returns analytics depending on the selected period.
     * Supported periods: day | month | year
     */
    @Transactional(readOnly = true)
    @GetMapping("/{period}")
    public ResponseEntity<Map<String, Object>> getDashboardStats(
            @PathVariable String period,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        log.info("📊 [DashboardController] Fetching dashboard stats for period = {}", period);

        Map<String, Object> response = new HashMap<>();
        List<Order> orders = orderRepository.findAll();
        List<OrderDetails> allOrderDetails = orders.stream()
                .flatMap(o -> o.getOrderDetails().stream())
                .toList();

        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<OrderDetails> filteredOrders = filterByPeriod(allOrderDetails, period, targetDate);

        response.put("mostSoldProduct", findMostSoldProduct(filteredOrders));
        response.put("topCategories", findTopSoldCategories(filteredOrders));
        response.put("bestRatedProducts", getBestRatedProducts());

        // Summary
        response.put("summary", Map.of(
                "totalOrders", orders.size(),
                "totalProducts", productRepository.count(),
                "totalCategories", categoryRepository.count()
        ));

        log.info("✅ Dashboard data ready for {}", period);
        return ResponseEntity.ok(response);
    }

    private List<OrderDetails> filterByPeriod(List<OrderDetails> details, String period, LocalDate date) {
        return details.stream().filter(od -> {
            LocalDateTime created = od.getOrder().getCreatedDate();
            if (created == null) return false;
            return switch (period.toLowerCase()) {
                case "day" -> created.toLocalDate().equals(date);
                case "month" -> YearMonth.from(created).equals(YearMonth.from(date));
                case "year" -> created.getYear() == date.getYear();
                default -> false;
            };
        }).toList();
    }

    private MostSoldProductDTO findMostSoldProduct(List<OrderDetails> orderDetails) {
        return orderDetails.stream()
                .collect(Collectors.groupingBy(OrderDetails::getProduct, Collectors.summingInt(OrderDetails::getQuantity)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    Product p = entry.getKey();
                    int totalSold = entry.getValue();
                    String imageUrl = p.getFirstImageUrl().orElse(null);
                    double rate = p.getRate();
                    return new MostSoldProductDTO(
                            p.getId().longValue(),
                            p.getName(),
                            imageUrl,
                            totalSold,
                            rate
                    );
                })
                .orElse(null);
    }

    private List<MostSoldCategoryDTO> findTopSoldCategories(List<OrderDetails> orderDetails) {
        return orderDetails.stream()
                .collect(Collectors.groupingBy(od -> od.getProduct().getCategory(), Collectors.summingInt(OrderDetails::getQuantity)))
                .entrySet().stream()
                .sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> new MostSoldCategoryDTO(entry.getKey().getId().longValue(),
                        entry.getKey().getName(),
                        entry.getValue()))
                .toList();
    }


    private List<BestRatedProductDTO> getBestRatedProducts() {
        return productRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(Product::getRate).reversed())
                .limit(5)
                .map(p -> new BestRatedProductDTO(
                        p.getId().longValue(),
                        p.getName(),
                        p.getFirstImageUrl().orElse(null),
                        p.getRate()
                ))
                .toList();
    }

}
