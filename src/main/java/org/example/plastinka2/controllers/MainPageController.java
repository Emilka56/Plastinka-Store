package org.example.plastinka2.controllers;

import org.example.plastinka2.models.Product;
import org.example.plastinka2.models.User;
import org.example.plastinka2.repository.ProductRepository;
import org.example.plastinka2.repository.UserRepository;
import org.example.plastinka2.services.ProductService;
import org.example.plastinka2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainPageController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;


    @GetMapping("/mainPage")
    public String getMainPage(Model model, Principal principal) {

        if (principal != null) {
            // Получаем User из вашего репозитория по email
            User user = userService.findByEmail(principal.getName());
            model.addAttribute("user", user);
        }

        List<Product> products = new ArrayList<>();
        products = productService.findAll();

        List<String> genres = products.stream()
                .map(p -> p.getGenre())
                .filter(genre -> genre != null && !genre.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("genres", genres);
        model.addAttribute("products", products);

        return "main_page";
    }
}
