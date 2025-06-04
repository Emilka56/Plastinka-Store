package org.example.plastinka2.controllers;

import lombok.RequiredArgsConstructor;
import org.example.plastinka2.models.Product;
import org.example.plastinka2.models.User;
import org.example.plastinka2.services.ITunesService;
import org.example.plastinka2.services.ProductService;
import org.example.plastinka2.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainPageController {

    private final UserService userService;
    private final ProductService productService;
    private final ITunesService itunesService;
    private static final Logger logger = LoggerFactory.getLogger(MainPageController.class);

    @GetMapping("/mainPage")
    public String getMainPage(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            model.addAttribute("user", user);
            model.addAttribute("userRole", user.getRole().toString());
        }

        var products = productService.findAll();
        model.addAttribute("products", products);
        
        // Получаем уникальные жанры из продуктов
        List<String> genres = products.stream()
                .map(Product::getGenre)
                .filter(genre -> genre != null && !genre.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        
        model.addAttribute("genres", genres);
        
        return "main_page";
    }

    @PostMapping("/mainPage/load")
    @ResponseBody
    public ResponseEntity<List<Product>> loadMoreProducts(
            @RequestParam("genre") String genre,
            @RequestParam(value = "count", defaultValue = "5") int count) {
        try {
            logger.info("Loading more products for genre: {} with count: {}", genre, count);
            List<Product> products = itunesService.importAlbumsByGenre(genre, count);
            
            if (products.isEmpty()) {
                logger.warn("No products found for genre: {}", genre);
                return ResponseEntity.ok(List.of());
            }
            
            logger.info("Found {} products for genre: {}", products.size(), genre);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Error loading more products for genre: {}", genre, e);
            return ResponseEntity.ok(List.of());
        }
    }
}
