package com.jeyah.jeyahshopapi.cart;

import com.jeyah.jeyahshopapi.auth.AuthUtils;
import com.jeyah.jeyahshopapi.product.Product;
import com.jeyah.jeyahshopapi.product.ProductRepository;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = false)
    public CartResponse getCurrentUserCart() {
        User currentUser = AuthUtils.getCurrentUser(userRepository, entityManager);
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> createCartForUser(currentUser));
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse addProductToCart(Integer productId, Integer quantity) {
        // 1️⃣ Get managed User entity
        User user = AuthUtils.getCurrentUser(userRepository, entityManager);

        // 2️⃣ Get managed Cart entity (or create one if none exists)
        Cart managedCart = cartRepository.findByUser(user)
                .orElseGet(() -> createCartForUser(user));

        // Re-fetch cart to ensure it's managed (final for lambda)
        managedCart = cartRepository.findById(managedCart.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        Cart finalCart = managedCart; // ✅ effectively final for lambda

        // 3️⃣ Get product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 4️⃣ Find existing CartItem or create a new one
        CartItem cartItem = cartItemRepository.findByCartAndProduct(finalCart, product)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(finalCart);   // ✅ use finalCart
                    newItem.setProduct(product);
                    newItem.setProductQuantity(0);
                    return newItem;
                });

        // 5️⃣ Update quantity
        cartItem.setProductQuantity(cartItem.getProductQuantity() + quantity);
        cartItemRepository.save(cartItem);

        // 6️⃣ Recalculate total
        recalculateCartTotal(finalCart);

        // 7️⃣ Save managed cart
        return CartResponse.from(cartRepository.save(finalCart));
    }


    @Transactional
    public CartResponse updateCartItemQuantity(Integer cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByIdWithCart(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItem.setProductQuantity(quantity);
        cartItemRepository.save(cartItem);

        recalculateCartTotal(cartItem.getCart());

        return CartResponse.from(cartRepository.save(cartItem.getCart()));
    }

    @Transactional
    public CartResponse removeCartItem(Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findByIdWithCart(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        recalculateCartTotal(cart);
        return CartResponse.from(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse clearCart() {
        User user = AuthUtils.getCurrentUser(userRepository, entityManager);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.setTotalPrice(0);
        return CartResponse.from(cartRepository.save(cart));
    }

    private Cart createCartForUser(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setTotalPrice(0);
        newCart.setCartItems(new ArrayList<>()); // ✅ initialize empty list
        return cartRepository.save(newCart);
    }


    private void recalculateCartTotal(Cart cart) {
        int total = cartItemRepository.findByCart(cart).stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getProductQuantity())
                .sum();
        cart.setTotalPrice(total);
    }


}
