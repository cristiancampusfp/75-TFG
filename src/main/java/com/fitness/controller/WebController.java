package com.fitness.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // Rutas públicas
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // Carga src/main/resources/templates/login.html
    }

    @GetMapping("/register")
    public String mostrarRegistro() {
        return "register"; // Carga src/main/resources/templates/register.html
    }

    // Rutas protegidas (El JWT y Spring Security se encargan de que no entre cualquiera)
    @GetMapping("/dashboard-admin")
    public String mostrarDashboardAdmin() {
        return "dashboard-admin"; // Carga src/main/resources/templates/dashboard-admin.html
    }

    @GetMapping("/dashboard-cliente")
    public String mostrarDashboardCliente() {
        return "dashboard-cliente"; // Carga src/main/resources/templates/dashboard-cliente.html
    }
}