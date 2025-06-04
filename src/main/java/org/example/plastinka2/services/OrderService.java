package org.example.plastinka2.services;

import org.example.plastinka2.dto.OrderDto;
import org.example.plastinka2.models.Order;
import org.example.plastinka2.models.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
//    Long saveToDataBaseAndGetId(OrderDto orders) ;
    OrderDto getActiveOrderForUser(Long userId) ;
    OrderDto createNewOrder(Long userId);
    void updateOrderWithAddress(Order order, Long addressId);
    Order createOrder(User user);
    Order findById(Long id);
    List<Order> findByUser(User user);
    void save(Order order);

//    void updateOrderWithAddress(Order order, Long addressId) ;
//    void setInactiveOrder(Long ordersId) ;
//    List<Order> findAllOrdersByUserId(Long userId);

//    List<Order> findAll();
//    void removeById(Long orderId) ;
}
