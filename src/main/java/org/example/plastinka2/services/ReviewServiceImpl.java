package org.example.plastinka2.services;

import org.example.plastinka2.models.Product;
import org.example.plastinka2.models.Review;
import org.example.plastinka2.models.User;
import org.example.plastinka2.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional(readOnly = true)
    public List<Review> getProductReviews(Long productId) {
        try {
            logger.info("Getting reviews for product id: {}", productId);
            List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
            logger.info("Found {} reviews for product {}", reviews.size(), productId);
            
            // Проверяем каждый отзыв для диагностики
            for (Review review : reviews) {
                logger.debug("Review details - ID: {}, Text: '{}', User: {}, Product: {}, Created: {}, Likes: {}", 
                    review.getId(),
                    review.getText(),
                    review.getUser() != null ? review.getUser().getEmail() + " (ID: " + review.getUser().getId() + ")" : "null",
                    review.getProduct() != null ? "Product " + review.getProduct().getId() : "null",
                    review.getCreatedAt(),
                    review.getLikes() != null ? review.getLikes().size() : 0
                );
            }
            
            return reviews;
        } catch (Exception e) {
            logger.error("Error getting reviews for product {}: {}", productId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Review addReview(Long productId, String text, Integer rating, User user) {
        try {
            logger.info("Adding review for product {} by user {}", productId, user.getId());
            
            Product product = productService.findById(productId);
            if (product == null) {
                logger.error("Product not found with id: {}", productId);
                throw new RuntimeException("Товар не найден");
            }

            Review review = Review.builder()
                    .text(text)
                    .rating(rating)
                    .user(user)
                    .product(product)
                    .build();

            Review savedReview = reviewRepository.save(review);
            logger.info("Successfully saved review with id: {}", savedReview.getId());
            
            return savedReview;
        } catch (Exception e) {
            logger.error("Error adding review for product {}: {}", productId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void toggleLike(Long reviewId, User user) {
        try {
            logger.info("Toggling like for review {} by user {}", reviewId, user.getId());
            
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Отзыв не найден"));

            if (review.isLikedByUser(user)) {
                review.removeLike(user);
                logger.info("Removed like from review {} by user {}", reviewId, user.getId());
            } else {
                review.addLike(user);
                logger.info("Added like to review {} by user {}", reviewId, user.getId());
            }

            reviewRepository.save(review);
            logger.info("Successfully saved review after toggling like");
        } catch (Exception e) {
            logger.error("Error toggling like for review {}: {}", reviewId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewed(Long productId, Long userId) {
        try {
            logger.info("Checking if user {} has reviewed product {}", userId, productId);
            boolean hasReviewed = reviewRepository.existsByUserIdAndProductId(userId, productId);
            logger.info("User {} {} reviewed product {}", userId, hasReviewed ? "has" : "has not", productId);
            return hasReviewed;
        } catch (Exception e) {
            logger.error("Error checking if user {} has reviewed product {}: {}", userId, productId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Review findById(Long reviewId) {
        try {
            logger.info("Getting review by id: {}", reviewId);
            return reviewRepository.findById(reviewId).orElse(null);
        } catch (Exception e) {
            logger.error("Error getting review by id {}: {}", reviewId, e.getMessage(), e);
            throw e;
        }
    }
} 