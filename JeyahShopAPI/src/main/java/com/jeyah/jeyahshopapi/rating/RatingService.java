package com.jeyah.jeyahshopapi.rating;

import com.jeyah.jeyahshopapi.order.OrderDetailsRepository;
import com.jeyah.jeyahshopapi.order.OrderStatus;
import com.jeyah.jeyahshopapi.product.Product;
import com.jeyah.jeyahshopapi.product.ProductRepository;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    public RatingResponse addOrUpdateRating(Integer userId, RatingRequest request) {
        System.out.println("addOrUpdateRating called with userId=" + userId + ", productId=" + request.getProductId() + ", rating=" + request.getRate());

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    System.out.println("User with id=" + userId + " not found");
                    return new RuntimeException("User not found");
                });
        System.out.println("User found: " + user.getEmail());

        // Find product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> {
                    System.out.println("Product with id=" + request.getProductId() + " not found");
                    return new RuntimeException("Product not found");
                });
        System.out.println("Product found: " + product.getName());

        // Check if purchased
        boolean purchased = orderDetailsRepository.existsByUserIdAndProductIdAndOrderStatus(
                userId, product.getId(), OrderStatus.DELIVERED
        );
        System.out.println("User purchased product? " + purchased);
        if (!purchased) {
            System.out.println("User " + userId + " tried to rate product " + product.getId() + " without purchasing");
            throw new RuntimeException("You can only rate products you have received");
        }

        // Find existing rating
        Rating rating = ratingRepository.findByUserIdAndProductId(userId, product.getId())
                .orElse(new Rating());
        if (rating.getId() != null) {
            System.out.println("Existing rating found: " + rating.getRating());
        } else {
            System.out.println("No existing rating found, creating new rating");
        }

        rating.setProduct(product);
        rating.setUser(user);
        rating.setRating(request.getRate());

        ratingRepository.save(rating);
        System.out.println("Rating saved: userId=" + userId + ", productId=" + product.getId() + ", rating=" + request.getRate());

        // Calculate average
        double averageRate = product.getRatings().stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(request.getRate());
        System.out.println("New average rating for product " + product.getId() + ": " + averageRate);

        return new RatingResponse(product.getId(), user.getId(), rating.getRating(), averageRate);
    }

    public List<RatingResponse> getRatingsForProduct(Integer productId) {
        return ratingRepository.findByProductId(productId).stream()
                .map(r -> new RatingResponse(
                        r.getProduct().getId(),
                        r.getUser().getId(),
                        r.getRating(),
                        r.getProduct().getRate()))
                .toList();
    }
}
