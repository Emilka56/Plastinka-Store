package org.example.plastinka2.repository;

import org.example.plastinka2.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByOrderId(Long orderId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Cart c SET c.totalQuantity = :quantity WHERE c.order.id = :orderId")
    void updateTotalQuantity(@Param("orderId") Long orderId, @Param("quantity") Integer quantity);
} 