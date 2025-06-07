package org.example.plastinka2.services;

import org.example.plastinka2.models.Review;
import org.example.plastinka2.models.User;

import java.util.List;

public interface ReviewService {
    List<Review> getProductReviews(Long productId);
    Review addReview(Long productId, String text, Integer rating, User user);
    void toggleLike(Long reviewId, User user);
    boolean hasUserReviewed(Long productId, Long userId);
    Review findById(Long reviewId);
} 