package org.example.plastinka2.controllers;

import org.example.plastinka2.converters.DateConverter;
import org.example.plastinka2.models.Product;
import org.example.plastinka2.models.Review;
import org.example.plastinka2.models.User;
import org.example.plastinka2.services.ProductService;
import org.example.plastinka2.services.ReviewService;
import org.example.plastinka2.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private DateConverter dateConverter;

    @GetMapping("/product/{id:[\\d]+}")
    public String showProduct(@PathVariable Long id, Model model) {
        try {
            logger.info("Showing product page for product id: {}", id);
            Product product = productService.getProductById(id);
            
            if (product == null) {
                logger.warn("Product not found with id: {}", id);
                return "redirect:/";
            }
            
            final User currentUser = getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("user", currentUser);
            }
            
            List<Review> reviews = reviewService.getProductReviews(id);
            logger.info("Retrieved {} reviews for product {}", reviews.size(), id);
            
            // Добавляем информацию о лайках
            reviews.forEach(review -> {
                if (currentUser != null) {
                    model.addAttribute("review_" + review.getId() + "_liked", review.isLikedByUser(currentUser));
                }
            });
            
            model.addAttribute("reviews", reviews);
            
            if (currentUser != null) {
                boolean hasUserReviewed = reviewService.hasUserReviewed(id, currentUser.getId());
                model.addAttribute("hasUserReviewed", hasUserReviewed);
            }
            
            model.addAttribute("product", product);
            return "product_page";
        } catch (Exception e) {
            logger.error("Error showing product page for id: {}", id, e);
            return "error";
        }
    }

    @GetMapping("/products")
    public String getAllProducts(Model model) {
        try {
            List<Product> products = productService.findAll();
            model.addAttribute("products", products);
            return "products";
        } catch (Exception e) {
            logger.error("Error getting all products", e);
            return "error";
        }
    }

    private User getCurrentUser() {
        try {
            return userService.getUserFromSession();
        } catch (Exception e) {
            return null;
        }
    }

    // ... остальные методы контроллера
}