package org.example.plastinka2.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    
    @GetMapping("/")
    public String redirectToMainPage() {
        return "redirect:/mainPage";
    }
} 