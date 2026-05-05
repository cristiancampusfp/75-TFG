package com.fitness.controller;

import com.fitness.dto.RutinaDTO;
import com.fitness.service.RutinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutinas")
@RequiredArgsConstructor
public class RutinaController {

    private final RutinaService rutinaService;

    // 1. Ver MIS rutinas (El cliente entra a su panel y carga su tabla)
    @GetMapping
    public ResponseEntity<List<RutinaDTO>> misRutinas() {
        // Sacamos el email del usuario logueado directamente del Token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return ResponseEntity.ok(rutinaService.obtenerRutinasDeUsuario(email));
    }

    // 2. Botón mágico: Generar rutina automática
    @PostMapping("/generar")
    public ResponseEntity<RutinaDTO> generarRutinaAuto() {
        // Sacamos el email del usuario logueado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        RutinaDTO nuevaRutina = rutinaService.generarRutinaAuto(email);
        return new ResponseEntity<>(nuevaRutina, HttpStatus.CREATED);
    }
}