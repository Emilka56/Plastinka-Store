//package org.example.plastinka2.controllers;
//
//import org.example.tuganash.models.Dish;
//import org.example.tuganash.services.DishService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.math.BigDecimal;
//
//@Controller
//@RequestMapping("/admin")
//public class AdminController {
//
//    @Autowired
//    private DishService dishService;
//
//    @GetMapping
//    public String adminPanel() {
//        return "admin_panel";
//    }
//
//    @PostMapping("/dishes")
//    public String addDish(
//            @RequestParam String name,
//            @RequestParam BigDecimal price,
//            @RequestParam String description,
//            RedirectAttributes redirectAttributes) {
//
//        try {
//            Dish dish = new Dish();
//            dish.setName(name);
//            dish.setPrice(price);
//            dish.setDescription(description);
//
//            dishService.addDish(dish);
//            redirectAttributes.addFlashAttribute("success", "Блюдо добавлено!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
//        }
//
//        return "redirect:/mainPage";
//    }
//
//    @DeleteMapping("/dishes/{id}")
//    public ResponseEntity<String> deleteDish(@PathVariable Long id) {
//        try {
//            dishService.deleteDishById(id);
//            return ResponseEntity.ok("Блюдо успешно удалено");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ошибка: " + e.getMessage());
//        }
//    }
//
//}