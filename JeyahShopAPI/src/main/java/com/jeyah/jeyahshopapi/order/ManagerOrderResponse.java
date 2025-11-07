package com.jeyah.jeyahshopapi.order;

import java.time.LocalDateTime;
import java.util.List;

public class ManagerOrderResponse {

    private Long id;           // order ID
    private String userEmail;  // customer's email
    private LocalDateTime createdAt;
    private OrderStatus status;
    private List<OrderDetailsResponse> products;
    private Double totalPrice;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public List<OrderDetailsResponse> getProducts() { return products; }
    public void setProducts(List<OrderDetailsResponse> products) { this.products = products; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}
