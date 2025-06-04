package org.example.plastinka2.repository;

import org.example.plastinka2.dto.OrderDto;
import org.example.plastinka2.models.Order;
import org.example.plastinka2.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT new org.example.plastinka2.dto.OrderDto(o.user.id, o.id) FROM Order o WHERE o.id = :orderId")
    OrderDto findOrderDtoById(@Param("orderId") Long orderId);

    @Query("SELECT new org.example.plastinka2.dto.OrderDto(o.user.id, o.id) FROM Order o WHERE o.user.id = :userId AND o.isActive = true")
    OrderDto getActiveOrderForUser(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.isActive = false WHERE o.id = :orderId")
    void setInactiveOrder(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    List<Order> findAllOrdersByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Order o WHERE o.id = :orderId")
    void removeById(@Param("orderId") Long orderId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO order_address (order_id, address_id) VALUES (:orderId, :addressId)", nativeQuery = true)
    void updateOrderByAddressId(@Param("orderId") Long orderId, @Param("addressId") Long addressId);

    List<Order> findByUserOrderByCreatedAtDesc(User user);
}
