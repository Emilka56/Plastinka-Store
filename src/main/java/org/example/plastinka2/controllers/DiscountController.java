package org.example.plastinka2.controllers;

import org.example.plastinka2.models.Discount;
import org.example.plastinka2.models.Role;
import org.example.plastinka2.models.User;
import org.example.plastinka2.services.DiscountService;
import org.example.plastinka2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @Autowired
    private UserService userService;

    @GetMapping("/manage")
    public String manageDiscounts(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)) {
            return "redirect:/";
        }

        model.addAttribute("user", user);
        model.addAttribute("discounts", discountService.getAllDiscounts());
        return "manage";
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createDiscount(
            Principal principal,
            @RequestParam String name,
            @RequestParam BigDecimal percentageOff,
            @RequestParam(required = false) String promoCode) {

        if (principal == null) {
            return ResponseEntity.badRequest().body("Необходима авторизация");
        }

        User user = userService.findByEmail(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.badRequest().body("Недостаточно прав");
        }

        try {
            Discount discount = discountService.createDiscount(name, percentageOff, promoCode);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("discount", discount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getDiscount(
            Principal principal,
            @PathVariable Long id) {

        if (principal == null) {
            return ResponseEntity.badRequest().body("Необходима авторизация");
        }

        User user = userService.findByEmail(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.badRequest().body("Недостаточно прав");
        }

        try {
            Discount discount = discountService.getDiscountById(id);
            return ResponseEntity.ok(discount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateDiscount(
            Principal principal,
            @PathVariable Long id,
            @RequestBody Map<String, Object> requestData) {

        if (principal == null) {
            return ResponseEntity.badRequest().body("Необходима авторизация");
        }

        User user = userService.findByEmail(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.badRequest().body("Недостаточно прав");
        }

        try {
            String name = (String) requestData.get("name");
            BigDecimal percentageOff = new BigDecimal(requestData.get("percentageOff").toString());
            String promoCode = requestData.get("promoCode") != null ?
                    (String) requestData.get("promoCode") : null;

            Discount discount = discountService.updateDiscount(
                    id, name, percentageOff, promoCode);  // Updated method call

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("discount", discount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteDiscount(
            Principal principal,
            @PathVariable Long id) {

        if (principal == null) {
            return ResponseEntity.badRequest().body("Необходима авторизация");
        }

        User user = userService.findByEmail(principal.getName());
        if (!user.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.badRequest().body("Недостаточно прав");
        }

        try {
            discountService.deleteDiscount(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/apply-promo")
    @ResponseBody
    public ResponseEntity<?> applyPromoCode(
            Principal principal,
            @RequestParam String promoCode,
            @RequestParam BigDecimal originalPrice) {

        if (principal == null) {
            return ResponseEntity.badRequest().body("Необходима авторизация");
        }

        try {
            User user = userService.findByEmail(principal.getName());
            BigDecimal discountedPrice = discountService.calculateDiscountedPrice(
                    originalPrice, user.getId(), promoCode);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("discountedPrice", discountedPrice);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}