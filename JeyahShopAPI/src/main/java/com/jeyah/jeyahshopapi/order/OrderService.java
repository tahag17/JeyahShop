package com.jeyah.jeyahshopapi.order;

import com.jeyah.jeyahshopapi.auth.AuthUtils;
import com.jeyah.jeyahshopapi.cart.Cart;
import com.jeyah.jeyahshopapi.cart.CartItemRepository;
import com.jeyah.jeyahshopapi.cart.CartRepository;
import com.jeyah.jeyahshopapi.common.PageResponse;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Transactional(readOnly = true)
    public List<Order> getCurrentUserOrders() {
        User user = AuthUtils.getCurrentUser(userRepository);
        return orderRepository.findByUser(user);
    }

    @Transactional
    public Order makeOrder() {
        User user = AuthUtils.getCurrentUser(userRepository);
        log.info("Making order for user: {}", user.getId());

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        log.info("User's cart: {}", cart.getId());

        if (cart.getCartItems().isEmpty()) {
            log.warn("Cart is empty!");
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        List<OrderDetails> detailsList = cart.getCartItems().stream().map(item -> {
            OrderDetails details = new OrderDetails();
            details.setOrder(order);
            details.setProduct(item.getProduct());
            details.setQuantity(item.getProductQuantity());
            details.setPrice(item.getProduct().getPrice());
            details.setUser(user);
            log.info("Prepared OrderDetails for product {} with quantity {}", item.getProduct().getId(), item.getProductQuantity());
            return details;
        }).toList();

        order.setOrderDetails(detailsList);

        log.info("Saving order with {} details", detailsList.size());
        orderRepository.save(order);
        orderDetailsRepository.saveAll(detailsList);
        log.info("Order saved with ID {}", order.getId());

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);

        return order;
    }

    @Transactional
    public Order cancelOrder(Integer orderId) {
        User user = AuthUtils.getCurrentUser(userRepository);
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrder(Integer orderId) {
        User user = AuthUtils.getCurrentUser(userRepository);
        return orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    //----------------MANAGER METHODS-----------------
    public PageResponse<OrderResponse> findAllOrders(int page, int size) {
        Page<Order> orders = orderRepository.findAll(PageRequest.of(page, size));
        return PageResponse.from(orders, OrderResponse::from);
    }

    public OrderResponse findOrderById(Integer id) {
        return OrderResponse.from(orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found")));
    }

    @Transactional
    public OrderResponse updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancelOrderAsManager(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.CANCELLED);
        return OrderResponse.from(orderRepository.save(order));
    }
}
