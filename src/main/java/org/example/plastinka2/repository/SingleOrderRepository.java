package org.example.plastinka2.repository;

import org.example.plastinka2.models.SingleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SingleOrderRepository extends JpaRepository<SingleOrder, Long> {
    @Query("SELECT s.quantity FROM SingleOrder s WHERE s.order.id = :orderId AND s.product.id = :productId")
    Integer getQuantityOfProduct(@Param("orderId") Long orderId, @Param("productId") Long productId);

    @Modifying
    @Query("UPDATE SingleOrder s SET s.quantity = :quantity WHERE s.order.id = :orderId AND s.product.id = :productId")
    void updateQuantity(@Param("orderId") Long orderId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Query("SELECT s FROM SingleOrder s WHERE s.order.id = :orderId AND s.product.id = :productId")
    SingleOrder findByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);

    @Query("SELECT COUNT(s) > 0 FROM SingleOrder s WHERE s.order.id = :orderId AND s.product.id = :productId")
    boolean existsByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);

    @Query("SELECT s FROM SingleOrder s WHERE s.order.id = :orderId")
    List<SingleOrder> findByOrderId(@Param("orderId") Long orderId);

    @Modifying
    @Query("DELETE FROM SingleOrder s WHERE s.order.id = :orderId AND s.product.id = :productId")
    void removeByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);
}
