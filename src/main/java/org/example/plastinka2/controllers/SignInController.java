package org.example.plastinka2.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignInController {
    @GetMapping("/signIn")

    public String signIn(Model model, @RequestParam(name = "confirmEmail", required = false) String confirmEmail) {
        model.addAttribute("confirmEmail", confirmEmail);
        return "sign_in_page";
    }
}
