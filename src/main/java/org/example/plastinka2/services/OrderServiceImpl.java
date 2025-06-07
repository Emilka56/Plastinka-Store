package org.example.plastinka2.services;

import org.example.plastinka2.dto.OrderDto;
import org.example.plastinka2.models.Order;
import org.example.plastinka2.models.User;
import org.example.plastinka2.models.Cart;
import org.example.plastinka2.repository.OrderRepository;
import org.example.plastinka2.repository.UserRepository;
import org.example.plastinka2.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    @Transactional
    public OrderDto createNewOrder(Long userId) {
        try {
            logger.info("Creating new order for user {}", userId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = Order.builder()
                    .user(user)
                    .isActive(true)
                    .totalPrice(BigDecimal.ZERO)
                    .createdAt(LocalDateTime.now())
                    .build();

            order = orderRepository.save(order);

            // Создаем корзину для заказа
            Cart cart = Cart.builder()
                    .order(order)
                    .totalQuantity(0)
                    .build();
            cartRepository.save(cart);
            
            OrderDto orderDto = OrderDto.builder()
                    .userId(userId)
                    .orderId(order.getId())
                    .build();
            
            logger.info("Successfully created new order with id {} and cart", order.getId());
            return orderDto;
        } catch (Exception e) {
            logger.error("Error creating new order for user {}", userId, e);
            throw new RuntimeException("Error creating new order", e);
        }
    }

//    @Override
//    public Long saveToDataBaseAndGetId(OrderDto orders) {
//        try {
//            logger.info("Saving order");
//            return orderRepository.saveToDataBaseAndGetId(orders);
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            throw e;
//        }
//    }

    @Override
    public OrderDto getActiveOrderForUser(Long userId) {
        try {
            return orderRepository.getActiveOrderForUser(userId);
        } catch (Exception e) {
            logger.error("Error getting active order for user {}", userId, e);
            throw e;
        }
    }

    @Override
    public void updateOrderWithAddress(Order order, Long addressId) {
        try {
            logger.info("Updating order {} with address {}", order.getId(), addressId);
            orderRepository.updateOrderByAddressId(order.getId(), addressId);
            logger.info("Successfully updated order with address");
        } catch (Exception e) {
            logger.error("Error updating order {} with address {}", order.getId(), addressId, e);
            throw new RuntimeException("Error updating order with address", e);
        }
    }

//    @Override
//    public void setInactiveOrder(Long ordersId) {
//        try{
//            orderRepository.setInactiveOrder(ordersId);
//        }catch (Exception e) {
//            logger.error(e.getMessage());
//            throw e;
//        }
//    }

//    @Override
//    public List<Order> findAllOrdersByUserId(Long userId) {
//        try{
//            return orderRepository.findAllOrdersByUserId(userId);
//        }catch (Exception e) {
//            logger.error(e.getMessage());
//            throw e;
//        }
//    }
//
//    @Override
//    public List<Order> findAll() {
//        try{
//            return orderRepository.findAll();
//        }catch (Exception e) {
//            logger.error(e.getMessage());
//            throw e;
//        }
//    }
//
//    @Override
//    public void removeById(Long orderId) {
//        try{
//            orderRepository.removeById(orderId);
//        }catch (Exception e) {
//            logger.error(e.getMessage());
//            throw e;
//        }
//    }
}
