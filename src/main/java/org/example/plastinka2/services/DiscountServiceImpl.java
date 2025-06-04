package org.example.plastinka2.services;

import org.example.plastinka2.models.Discount;
import org.example.plastinka2.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class DiscountServiceImpl implements DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Override
    public Discount createDiscount(String name, BigDecimal percentageOff, String promoCode) {
        Discount discount = Discount.builder()
                .name(name)
                .percentageOff(percentageOff)
                .promoCode(promoCode)
                .build();
        return discountRepository.save(discount);
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    @Override
    public void deleteDiscount(Long id) {
        discountRepository.deleteById(id);
    }

    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, Long userId, String promoCode) {
        if (promoCode == null || promoCode.isEmpty()) {
            return originalPrice;
        }

        return discountRepository.findByPromoCode(promoCode)
                .map(discount -> {
                    BigDecimal percentageOff = discount.getPercentageOff()
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    BigDecimal discountAmount = originalPrice.multiply(percentageOff);
                    return originalPrice.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
                })
                .orElse(originalPrice);
    }

    @Override
    public Discount findByPromoCode(String promoCode) {
        return discountRepository.findByPromoCode(promoCode)
                .orElseThrow(() -> new RuntimeException("Промокод не найден"));
    }

    @Override
    public Discount updateDiscount(Long id, String name, BigDecimal percentageOff, String promoCode) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Скидка не найдена"));

        discount.setName(name);
        discount.setPercentageOff(percentageOff);
        discount.setPromoCode(promoCode);

        return discountRepository.save(discount);
    }

    @Override
    public Discount getDiscountById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Промокод не найден"));
    }
}