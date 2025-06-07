package org.example.plastinka2.services;

import org.example.plastinka2.models.SingleOrder;
import java.util.List;

public interface SingleOrderService {
    void addProductToOrder(Long orderId, Long productId);
    void updateQuantity(Long orderId, Long productId, Integer quantity);
    void removeFromCart(Long orderId, Long productId);
    Integer getQuantityOfProduct(Long orderId, Long productId);
    SingleOrder findByOrderIdAndProductId(Long orderId, Long productId);
    List<SingleOrder> findByOrderId(Long orderId);
}
