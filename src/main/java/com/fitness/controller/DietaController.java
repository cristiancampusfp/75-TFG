package com.fitness.controller;

import com.fitness.dto.DietaRequestDTO;
import com.fitness.model.Dieta;
import com.fitness.model.RegistroPeso;
import com.fitness.service.DietaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/dieta")
@RequiredArgsConstructor
public class DietaController {

    private final DietaService dietaService;

    /**
     * Obtiene la dieta actual del usuario autenticado.
     * Útil para cargar los macros y el menú al entrar en la pestaña.
     */
    @GetMapping("/actual")
    public ResponseEntity<?> obtenerDietaActual(Principal principal) {
        try {
            Dieta dieta = dietaService.obtenerDietaPorEmail(principal.getName());
            return ResponseEntity.ok(dieta);
        } catch (Exception e) {
            // Si el usuario aún no tiene dieta, devolvemos un estado 204 (sin contenido)
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Calcula y guarda el plan nutricional inicial (macros y calorías).
     * Utiliza la fórmula de Mifflin-St Jeor y el reparto de Eric Helms.
     */
    @PostMapping("/configurar")
    public ResponseEntity<Dieta> configurarDieta(Principal principal, @RequestBody DietaRequestDTO dto) {
        Dieta dietaCalculada = dietaService.calcularYGuardarPlan(principal.getName(), dto);
        return ResponseEntity.ok(dietaCalculada);
    }

    /**
     * Solicita a la IA la generación de un menú diario detallado.
     * El menú se basa en los macros previamente calculados y los gustos del usuario.
     */
    @PostMapping("/generar-menu")
    public ResponseEntity<String> generarMenuIA(Principal principal) {
        String menuGenerado = dietaService.generarMenuConIA(principal.getName());
        return ResponseEntity.ok(menuGenerado);
    }

    /**
     * Registra el peso actual del usuario.
     * Si el usuario está en fase de "GANAR_MUSCULO" y el peso se estanca,
     * el sistema ajustará automáticamente las calorías.
     */
    @PostMapping("/registrar-peso")
    public ResponseEntity<String> registrarPeso(Principal principal, @RequestParam Double peso) {
        dietaService.registrarPesoSemanal(principal.getName(), peso);
        return ResponseEntity.ok("Peso registrado con éxito. Tu plan se ha actualizado según tu progreso.");
    }

    /**
     * 🔥 NUEVO: Obtiene el historial de pesos del usuario para mostrarlo en el panel.
     */
    @GetMapping("/pesos")
    public ResponseEntity<List<RegistroPeso>> obtenerHistorialPesos(Principal principal) {
        return ResponseEntity.ok(dietaService.obtenerHistorialPesos(principal.getName()));
    }
}