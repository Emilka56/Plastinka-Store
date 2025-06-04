package org.example.plastinka2.controllers;

import org.example.plastinka2.dto.OrderDto;
import org.example.plastinka2.models.*;
import org.example.plastinka2.services.*;
import org.example.plastinka2.converters.DateConverter;
import org.example.plastinka2.repository.OrderRepository;
import org.example.plastinka2.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private SingleOrderService singleOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
//    @Autowired
//    private OrderRepository orderRepository;
//    @Autowired
//    private AddressRepository addressRepository;
    @Autowired
    private DateConverter dateConverter;
    @Autowired
    private DiscountService discountService;
    @Autowired
    private AddressService addressService;


    @GetMapping
    public String showCart(Model model) {
        try {
            Long userId = userService.getUserFromSession().getId();
            OrderDto orderDto = orderService.getActiveOrderForUser(userId);
            
            if (orderDto != null) {
                List<SingleOrder> cartItems = singleOrderService.findByOrderId(orderDto.getOrderId());
                BigDecimal totalPrice = cartItems.stream()
                    .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("totalPrice", totalPrice);
                model.addAttribute("orderId", orderDto.getOrderId());
            }
            
            return "cart";
        } catch (Exception e) {
            logger.error("Error showing cart", e);
            return "error";
        }
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> addToCart(@RequestParam("productId") Long productId) {
        try {
            logger.info("Adding product {} to cart", productId);
            
            Long userId = userService.getUserFromSession().getId();
            logger.info("User ID: {}", userId);
            
            OrderDto orderDto = orderService.getActiveOrderForUser(userId);
            logger.info("Active order: {}", orderDto);
            
            if (orderDto == null) {
                logger.info("Creating new order for user {}", userId);
                orderDto = orderService.createNewOrder(userId);
            }
            
            logger.info("Adding product {} to order {}", productId, orderDto.getOrderId());
            singleOrderService.addProductToOrder(orderDto.getOrderId(), productId);
            
            logger.info("Updating total quantity for order {}", orderDto.getOrderId());
            cartService.updateTotalQuantity(orderDto.getOrderId());
            
            Integer totalQuantity = cartService.getTotalQuantity(orderDto.getOrderId());
            logger.info("Total quantity: {}", totalQuantity);
            
            Map<String, Integer> response = new HashMap<>();
            response.put("quantity", totalQuantity);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error adding product {} to cart", productId, e);
            Map<String, Integer> errorResponse = new HashMap<>();
            errorResponse.put("quantity", 0);
            return ResponseEntity.ok(errorResponse);
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateQuantity(
            @RequestParam("orderId") Long orderId,
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        try {
            if (quantity <= 0) {
                singleOrderService.removeFromCart(orderId, productId);
            } else {
                singleOrderService.updateQuantity(orderId, productId, quantity);
            }
            cartService.updateTotalQuantity(orderId);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating quantity for product {} in order {}", productId, orderId, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam("orderId") Long orderId,
            @RequestParam("productId") Long productId) {
        try {
            singleOrderService.removeFromCart(orderId, productId);
            cartService.updateTotalQuantity(orderId);
            
            return "redirect:/cart";
        } catch (Exception e) {
            logger.error("Error removing product {} from order {}", productId, orderId, e);
            return "error";
        }
    }

    @GetMapping("/product/{id}")
    public String showProductPage(@PathVariable("id") Long productId, Model model) {
        try {
            logger.info("Showing product page for product id: {}", productId);
            Product product = productService.findById(productId);
            model.addAttribute("product", product);
            return "product_page";
        } catch (Exception e) {
            logger.error("Error showing product page for id: {}", productId, e);
            return "error";
        }
    }

    @GetMapping(value = "/total-quantity", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getTotalQuantity() {
        try {
            Long userId = userService.getUserFromSession().getId();
            OrderDto orderDto = orderService.getActiveOrderForUser(userId);
            
            Integer totalQuantity = 0;
            if (orderDto != null) {
                List<SingleOrder> singleOrders = singleOrderService.findByOrderId(orderDto.getOrderId());
                totalQuantity = singleOrders.stream()
                    .mapToInt(SingleOrder::getQuantity)
                    .sum();
            }
            
            Map<String, Integer> response = new HashMap<>();
            response.put("quantity", totalQuantity);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting cart total quantity", e);
            Map<String, Integer> errorResponse = new HashMap<>();
            errorResponse.put("quantity", 0);
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping(value = "/format-date", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String formatDate(@RequestParam("date") String dateStr) {
        try {
            LocalDateTime date = LocalDateTime.parse(dateStr);
            return dateConverter.convert(date);
        } catch (Exception e) {
            logger.error("Error formatting date: {}", dateStr, e);
            return dateStr;
        }
    }

    @PostMapping("/complete-order")
    @ResponseBody
    public ResponseEntity<?> completeOrder(
            @RequestParam Long orderId,
            @RequestParam String deliveryDateStr,
            @RequestParam String street,
            @RequestParam String city,
            @RequestParam int houseNumber,
            @RequestParam int apartmentNumber,
            @RequestParam(required = false) String promoCode) {
        try {
            logger.info("Completing order {} with delivery date {}", orderId, deliveryDateStr);

            Order order = orderService.findById(orderId);

            // Создаем новый адрес
            Address orderAddress = Address.builder()
                    .street(street)
                    .city(city)
                    .house_number(houseNumber)
                    .apartment_number(apartmentNumber)
                    .build();
            addressService.save(orderAddress);

            // Парсим дату доставки
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime deliveryDate = LocalDateTime.parse(deliveryDateStr, formatter);
            order.setDeliveryTime(deliveryDate);

            // Добавляем адрес к заказу
            if (order.getAddresses() == null) {
                order.setAddresses(new ArrayList<>());
            }
            order.getAddresses().add(orderAddress);

            // Устанавливаем статус заказа как неактивный
            order.setActive(false);

            // Пересчитываем общую стоимость с правильным округлением
            BigDecimal totalPrice = order.getSingleOrders().stream()
                    .map(item -> item.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Применяем скидку, если есть промокод
            if (promoCode != null && !promoCode.isEmpty()) {
                totalPrice = discountService.calculateDiscountedPrice(totalPrice, order.getUser().getId(), promoCode);
            }
            order.setTotalPrice(totalPrice);

            // Сохраняем заказ
            orderService.save(order);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error completing order {}: {}", orderId, e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}