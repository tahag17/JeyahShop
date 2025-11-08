package com.jeyah.jeyahshopapi.rating;

import com.jeyah.jeyahshopapi.auth.AuthUtils;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> rateProduct(@RequestBody RatingRequest request) {
        // Get currently logged-in user
        User user = AuthUtils.getCurrentUser(userRepository);

        RatingResponse response = ratingService.addOrUpdateRating(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<RatingResponse>> getProductRatings(@PathVariable Integer productId) {
        return ResponseEntity.ok(ratingService.getRatingsForProduct(productId));
    }
}

