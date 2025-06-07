package org.example.plastinka2.controllers;

import org.example.plastinka2.models.Review;
import org.example.plastinka2.models.User;
import org.example.plastinka2.services.ReviewService;
import org.example.plastinka2.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addReview(
            @RequestParam("productId") Long productId,
            @RequestParam("text") String text,
            @RequestParam(value = "rating", defaultValue = "0") Integer rating) {
        try {
            logger.info("Adding review for product id: {} with text: {} and rating: {}", productId, text, rating);
            
            User user = userService.getUserFromSession();
            logger.info("User {} is adding review", user.getId());
            
            if (reviewService.hasUserReviewed(productId, user.getId())) {
                logger.warn("User {} has already reviewed product {}", user.getId(), productId);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Вы уже оставляли отзыв на этот товар");
                return ResponseEntity.badRequest().body(response);
            }

            Review review = reviewService.addReview(productId, text, rating, user);
            logger.info("Successfully added review with id: {}", review.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("reviewId", review.getId());
            response.put("userName", review.getUser().getFirstName() + " " + review.getUser().getLastName());
            response.put("text", review.getText());
            response.put("rating", review.getRating());
            response.put("createdAt", review.getCreatedAt().toString());
            response.put("likesCount", review.getLikesCount());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error adding review for product {}: {}", productId, e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/like")
    @ResponseBody
    public ResponseEntity<?> toggleLike(@RequestParam("reviewId") Long reviewId) {
        try {
            logger.info("Toggling like for review id: {}", reviewId);
            
            User user = userService.getUserFromSession();
            logger.info("User {} is toggling like", user.getId());
            
            reviewService.toggleLike(reviewId, user);
            
            // Получаем обновленный отзыв из базы данных
            Review review = reviewService.findById(reviewId);
            if (review == null) {
                logger.error("Review not found with id: {}", reviewId);
                throw new RuntimeException("Отзыв не найден");
            }
            
            logger.info("Review {} now has {} likes", reviewId, review.getLikesCount());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("likesCount", review.getLikesCount());
            response.put("isLiked", review.isLikedByUser(user));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error toggling like for review {}: {}", reviewId, e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 