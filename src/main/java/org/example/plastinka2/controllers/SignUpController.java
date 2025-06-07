package org.example.plastinka2.controllers;


import org.example.plastinka2.dto.UserForm;
import org.example.plastinka2.services.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SignUpController {

    @Autowired
    private SignUpService signUpService;

    @GetMapping("/signUp")
    public String getSignUpPage(){
        return "sign_up_page";
    }

    @PostMapping("/signUp")
    public String signUp(UserForm form, RedirectAttributes attributes) {
        System.out.println("************");
        System.out.println(form.getEmail());
        System.out.println(form.getPassword());
        signUpService.addUser(form);
        attributes.addAttribute("confirmEmail", "пожалуйста подтвердите свою почту");
        return "redirect:/signIn";
    }
}
