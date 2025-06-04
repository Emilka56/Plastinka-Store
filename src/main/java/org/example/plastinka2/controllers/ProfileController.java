package org.example.plastinka2.controllers;

import lombok.RequiredArgsConstructor;
import org.example.plastinka2.models.Order;
import org.example.plastinka2.models.User;
import org.example.plastinka2.services.OrderService;
import org.example.plastinka2.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/profile")
    public String getProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Order> orders = orderService.findByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        
        return "profile";
    }
} 