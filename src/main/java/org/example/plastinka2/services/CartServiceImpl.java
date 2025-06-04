package org.example.plastinka2.services;

import org.example.plastinka2.models.Cart;
import org.example.plastinka2.models.Order;
import org.example.plastinka2.models.Product;
import org.example.plastinka2.repository.CartRepository;
import org.example.plastinka2.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private OrderRepository orderRepository;


    @Override
    public Cart findByOrderId(Long orderId) {
        try {
            logger.info("Finding cart by order id: {}", orderId);
            return cartRepository.findByOrderId(orderId);
        } catch (Exception e) {
            logger.error("Error finding cart by order id: {}", orderId, e);
            throw new RuntimeException("Error finding cart", e);
        }
    }

    @Override
    @Transactional
    public void updateTotalQuantity(Long orderId) {
        try {
            logger.info("Updating total quantity for order id: {}", orderId);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            int totalQuantity = order.getSingleOrders().stream()
                    .mapToInt(singleOrder -> singleOrder.getQuantity())
                    .sum();
            
            Cart cart = cartRepository.findByOrderId(orderId);
            if (cart == null) {
                cart = Cart.builder()
                        .order(order)
                        .totalQuantity(totalQuantity)
                        .build();
                cartRepository.save(cart);
            } else {
                cartRepository.updateTotalQuantity(orderId, totalQuantity);
            }
            
            logger.info("Updated cart total quantity to {} for order {}", totalQuantity, orderId);
        } catch (Exception e) {
            logger.error("Error updating total quantity for order id: {}", orderId, e);
            throw new RuntimeException("Error updating cart quantity", e);
        }
    }

    @Override
    public void save(Cart cart) {
        try {
            logger.info("Saving cart for order id: {}", cart.getOrder().getId());
            cartRepository.save(cart);
        } catch (Exception e) {
            logger.error("Error saving cart", e);
            throw new RuntimeException("Error saving cart", e);
        }
    }

    @Override
    public Integer getTotalQuantity(Long orderId) {
        try {
            logger.info("Getting total quantity for order id: {}", orderId);
            Cart cart = cartRepository.findByOrderId(orderId);
            return cart != null ? cart.getTotalQuantity() : 0;
        } catch (Exception e) {
            logger.error("Error getting total quantity for order id: {}", orderId, e);
            throw new RuntimeException("Error getting cart quantity", e);
        }
    }

    @Override
    @Transactional
    public void addProductToCart(Long orderId, Product product) {
        try {
            logger.info("Adding product {} to cart for order {}", product.getId(), orderId);
            Cart cart = cartRepository.findByOrderId(orderId);
            if (cart != null) {
                if (!cart.getProducts().contains(product)) {
                    cart.getProducts().add(product);
                    cartRepository.save(cart);
                }
            }
            logger.info("Successfully added product to cart");
        } catch (Exception e) {
            logger.error("Error adding product {} to cart for order {}", product.getId(), orderId, e);
            throw new RuntimeException("Error adding product to cart", e);
        }
    }

    @Override
    @Transactional
    public void removeProductFromCart(Long orderId, Product product) {
        try {
            logger.info("Removing product {} from cart for order {}", product.getId(), orderId);
            Cart cart = cartRepository.findByOrderId(orderId);
            if (cart != null) {
                cart.getProducts().remove(product);
                cartRepository.save(cart);
            }
            logger.info("Successfully removed product from cart");
        } catch (Exception e) {
            logger.error("Error removing product {} from cart for order {}", product.getId(), orderId, e);
            throw new RuntimeException("Error removing product from cart", e);
        }
    }

    @Override
    public List<Product> getProductsInCart(Long orderId) {
        try {
            logger.info("Getting products in cart for order {}", orderId);
            Cart cart = cartRepository.findByOrderId(orderId);
            return cart != null ? cart.getProducts() : List.of();
        } catch (Exception e) {
            logger.error("Error getting products in cart for order {}", orderId, e);
            throw new RuntimeException("Error getting products in cart", e);
        }
    }
}
