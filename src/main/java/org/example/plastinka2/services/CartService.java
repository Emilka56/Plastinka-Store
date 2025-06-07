package org.example.plastinka2.services;

import org.example.plastinka2.models.Cart;
import org.example.plastinka2.models.Product;
import java.util.List;

public interface CartService {
    Cart findByOrderId(Long orderId);
    void updateTotalQuantity(Long orderId);
    void save(Cart cart);
    Integer getTotalQuantity(Long orderId);
    void addProductToCart(Long orderId, Product product);
    void removeProductFromCart(Long orderId, Product product);
    List<Product> getProductsInCart(Long orderId);
}
