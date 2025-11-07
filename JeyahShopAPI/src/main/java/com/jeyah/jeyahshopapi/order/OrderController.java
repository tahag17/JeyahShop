package com.jeyah.jeyahshopapi.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {
        List<Order> orders = orderService.getCurrentUserOrders();
        List<OrderResponse> response = orders.stream().map(OrderResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/make")
    public ResponseEntity<OrderResponse> makeOrder() {
        Order order = orderService.makeOrder();
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Integer orderId) {
        Order order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Integer orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

}
