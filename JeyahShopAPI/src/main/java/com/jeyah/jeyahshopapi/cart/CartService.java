package com.jeyah.jeyahshopapi.cart;

import com.jeyah.jeyahshopapi.auth.AuthUtils;
import com.jeyah.jeyahshopapi.product.Product;
import com.jeyah.jeyahshopapi.product.ProductRepository;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CartResponse getCurrentUserCart() {
        User currentUser = AuthUtils.getCurrentUser(userRepository);
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> createCartForUser(currentUser));
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse addProductToCart(Integer productId, Integer quantity) {
        User user = AuthUtils.getCurrentUser(userRepository);
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createCartForUser(user));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setProductQuantity(0);
                    return newItem;
                });

        cartItem.setProductQuantity(cartItem.getProductQuantity() + quantity);
        cartItemRepository.save(cartItem);

        recalculateCartTotal(cart);

        return CartResponse.from(cartRepository.save(cart));
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
        User user = AuthUtils.getCurrentUser(userRepository);
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
