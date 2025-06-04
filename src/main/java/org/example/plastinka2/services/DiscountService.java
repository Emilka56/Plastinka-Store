package org.example.plastinka2.services;

import org.example.plastinka2.models.Discount;
import java.math.BigDecimal;
import java.util.List;

public interface DiscountService {
    Discount createDiscount(String name, BigDecimal percentageOff, String promoCode);
    List<Discount> getAllDiscounts();
    void deleteDiscount(Long id);
    BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, Long userId, String promoCode);
    Discount findByPromoCode(String promoCode);
    Discount updateDiscount(Long id, String name, BigDecimal percentageOff, String promoCode);

    Discount getDiscountById(Long id);
} 