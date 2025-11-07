package com.jeyah.jeyahshopapi.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Integer orderId;
    private LocalDateTime createdAt; // use LocalDateTime to match BaseEntity
    private OrderStatus status;
    private List<OrderDetailsResponse> products;
    private Integer totalPrice;

    public static OrderResponse from(Order order) {
        List<OrderDetailsResponse> products = order.getOrderDetails().stream()
                .map(d -> new OrderDetailsResponse(
                        d.getProduct().getId(),
                        d.getProduct().getName(),
                        d.getQuantity(),
                        d.getPrice(),
                        d.getQuantity() * d.getPrice()
                )).toList();

        int total = products.stream().mapToInt(OrderDetailsResponse::getTotalPrice).sum();

        return new OrderResponse(
                order.getId(),
                order.getCreatedDate(), // ✅ use createdDate from BaseEntity
                order.getStatus(),
                products,
                total
        );
    }
}
