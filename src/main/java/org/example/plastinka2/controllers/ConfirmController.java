package org.example.plastinka2.controllers;


import org.example.plastinka2.models.User;
import org.example.plastinka2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ConfirmController {

    @Autowired
    private UserService userService;

    @GetMapping("/confirm/{code}")
    public String confirmUser(@PathVariable String code, RedirectAttributes attributes) {
        User user = userService.findByConfirmCode(code);

        if (user == null) {
            System.out.println("обработать ошибку");
            return null;
        }
        userService.deleteConfirmedCodeAndSave(user.getId());

        attributes.addAttribute("confirmEmail", "Аккаунт успешно подтвержден.");
        return "redirect:/signIn";
    }
}
