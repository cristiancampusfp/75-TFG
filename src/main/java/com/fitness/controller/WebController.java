package com.fitness.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/register")
    public String showRegisterPage() {
        // Esto le dice a Spring que busque el archivo "register.html" dentro de src/main/resources/templates/
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        // Esto busca "login.html"
        return "login";
    }
}