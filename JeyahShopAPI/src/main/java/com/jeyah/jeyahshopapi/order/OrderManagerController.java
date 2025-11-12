package com.jeyah.jeyahshopapi.order;

import com.jeyah.jeyahshopapi.auth.AuthUtils;
import com.jeyah.jeyahshopapi.common.PageResponse;
import com.jeyah.jeyahshopapi.exception.ErrorResponse;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manager/api/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class OrderManagerController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    // 1️⃣ Get all orders (paginated)
    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            PageResponse<ManagerOrderResponse> orders = orderService.findAllOrders(page, size);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des commandes."));
        }
    }

    // 2️⃣ Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        try {
            ManagerOrderResponse order = orderService.findOrderByIdManager(id);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Commande non trouvée."));
        }
    }

    // 3️⃣ Update order status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam OrderStatus status
    ) {
        try {
            User currentUser = AuthUtils.getCurrentUser(userRepository, entityManager);
            System.out.println("User " + currentUser.getEmail() + " is updating order " + id + " to " + status);

            ManagerOrderResponse updated = orderService.updateOrderStatusManager(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la mise à jour de la commande."));
        }
    }

    // 4️⃣ Cancel order
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id) {
        try {
            User currentUser = AuthUtils.getCurrentUser(userRepository, entityManager);
            System.out.println("User " + currentUser.getEmail() + " is cancelling order " + id);

            ManagerOrderResponse cancelled = orderService.cancelOrderAsManager(id);
            return ResponseEntity.ok(cancelled);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de l'annulation de la commande."));
        }
    }
}
