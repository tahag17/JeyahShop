package com.jeyah.jeyahshopapi.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/user/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final Logger log = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    // 1️⃣ Get current user's cart
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCurrentUserCart());
    }

    // 2️⃣ Add product to cart
    @PostMapping("/add/{productId}")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        log.info("[Controller] /add/{} called with quantity={}", productId, quantity);

        CartResponse response = cartService.addProductToCart(productId, quantity);

        log.info("[Controller] Cart response: {}", response);

        return ResponseEntity.ok(response);
    }

    // 3️⃣ Update quantity of a product in the cart
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Integer cartItemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(cartItemId, quantity));
    }

    // 4️⃣ Remove product from cart
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<CartResponse> removeCartItem(@PathVariable Integer cartItemId) {
        return ResponseEntity.ok(cartService.removeCartItem(cartItemId));
    }

    // 5️⃣ Clear the cart
    @DeleteMapping("/clear")
    public ResponseEntity<CartResponse> clearCart() {
        return ResponseEntity.ok(cartService.clearCart());
    }
}






