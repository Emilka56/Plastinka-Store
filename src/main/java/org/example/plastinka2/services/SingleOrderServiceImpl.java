package org.example.plastinka2.services;

import org.example.plastinka2.models.Order;
import org.example.plastinka2.models.Product;
import org.example.plastinka2.models.SingleOrder;
import org.example.plastinka2.repository.OrderRepository;
import org.example.plastinka2.repository.ProductRepository;
import org.example.plastinka2.repository.SingleOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SingleOrderServiceImpl implements SingleOrderService {
    private static final Logger logger = LoggerFactory.getLogger(SingleOrderServiceImpl.class);

    @Autowired
    private SingleOrderRepository singleOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public void addProductToOrder(Long orderId, Long productId) {
        try {
            logger.info("Adding product {} to order {}", productId, orderId);
            
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            SingleOrder singleOrder;
            if (singleOrderRepository.existsByOrderIdAndProductId(orderId, productId)) {
                singleOrder = singleOrderRepository.findByOrderIdAndProductId(orderId, productId);
                singleOrder.setQuantity(singleOrder.getQuantity() + 1);
            } else {
                singleOrder = SingleOrder.builder()
                        .order(order)
                        .product(product)
                        .quantity(1)
                        .build();
            }
            
            singleOrderRepository.save(singleOrder);
            logger.info("Successfully added/updated product in order");
        } catch (Exception e) {
            logger.error("Error adding product {} to order {}", productId, orderId, e);
            throw new RuntimeException("Error adding product to order", e);
        }
    }

    @Override
    @Transactional
    public void updateQuantity(Long orderId, Long productId, Integer quantity) {
        try {
            logger.info("Updating quantity for product {} in order {} to {}", productId, orderId, quantity);
            
            // Проверяем существование записи
            SingleOrder singleOrder = singleOrderRepository.findByOrderIdAndProductId(orderId, productId);
            if (singleOrder == null) {
                logger.error("SingleOrder not found for order {} and product {}", orderId, productId);
                throw new RuntimeException("SingleOrder not found");
            }
            
            singleOrderRepository.updateQuantity(orderId, productId, quantity);
            logger.info("Successfully updated quantity");
        } catch (Exception e) {
            logger.error("Error updating quantity for product {} in order {}", productId, orderId, e);
            throw new RuntimeException("Error updating quantity", e);
        }
    }

    @Override
    @Transactional
    public void removeFromCart(Long orderId, Long productId) {
        try {
            logger.info("Removing product {} from order {}", productId, orderId);
            singleOrderRepository.removeByOrderIdAndProductId(orderId, productId);
        } catch (Exception e) {
            logger.error("Error removing product {} from order {}", productId, orderId, e);
            throw new RuntimeException("Error removing product from cart", e);
        }
    }

    @Override
    public Integer getQuantityOfProduct(Long orderId, Long productId) {
        try {
            logger.info("Getting quantity for product {} in order {}", productId, orderId);
            return singleOrderRepository.getQuantityOfProduct(orderId, productId);
        } catch (Exception e) {
            logger.error("Error getting quantity for product {} in order {}", productId, orderId, e);
            throw new RuntimeException("Error getting quantity", e);
        }
    }

    @Override
    public SingleOrder findByOrderIdAndProductId(Long orderId, Long productId) {
        try {
            logger.info("Finding SingleOrder for order {} and product {}", orderId, productId);
            return singleOrderRepository.findByOrderIdAndProductId(orderId, productId);
        } catch (Exception e) {
            logger.error("Error finding SingleOrder for order {} and product {}", orderId, productId, e);
            throw new RuntimeException("Error finding SingleOrder", e);
        }
    }

    @Override
    public List<SingleOrder> findByOrderId(Long orderId) {
        try {
            logger.info("Finding all SingleOrders for order {}", orderId);
            return singleOrderRepository.findByOrderId(orderId);
        } catch (Exception e) {
            logger.error("Error finding SingleOrders for order {}", orderId, e);
            throw new RuntimeException("Error finding SingleOrders", e);
        }
    }
}
