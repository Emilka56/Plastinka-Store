package org.example.plastinka2.repository;

import org.example.plastinka2.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r " +
           "LEFT JOIN FETCH r.user u " +
           "LEFT JOIN FETCH r.product p " +
           "LEFT JOIN FETCH r.likes l " +
           "WHERE r.product.id = :productId " +
           "ORDER BY SIZE(r.likes) DESC, r.createdAt DESC")
    List<Review> findByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Query(value = "SELECT * FROM reviews WHERE id = :reviewId", nativeQuery = true)
    Review findReviewByIdNative(@Param("reviewId") Long reviewId);

    @Query(value = "SELECT column_name, data_type, character_maximum_length " +
           "FROM information_schema.columns " +
           "WHERE table_name = 'reviews'", nativeQuery = true)
    List<Object[]> getReviewsTableStructure();

    @Query(value = "SELECT r.*, p.id as product_id, u.id as user_id " +
           "FROM reviews r " +
           "LEFT JOIN product p ON r.product_id = p.id " +
           "LEFT JOIN users u ON r.user_id = u.id " +
           "WHERE r.product_id = :productId", nativeQuery = true)
    List<Object[]> findReviewsWithJoins(@Param("productId") Long productId);
}
