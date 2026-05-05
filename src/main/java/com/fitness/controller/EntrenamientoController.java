package com.fitness.controller;

import com.fitness.dto.EntrenamientoRequestDTO;
import com.fitness.dto.FuerzaMaximaDTO;
import com.fitness.dto.HistorialEntrenamientoDTO;
import com.fitness.service.EntrenamientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrenamientos")
@RequiredArgsConstructor
public class EntrenamientoController {

    private final EntrenamientoService entrenamientoService;

    @PostMapping("/guardar")
    public ResponseEntity<String> guardarEntrenamiento(
            Authentication authentication,
            @RequestBody EntrenamientoRequestDTO dto) {

        // Obtenemos el email del usuario logueado automáticamente desde su token de seguridad
        String email = authentication.getName();

        entrenamientoService.guardarEntrenamiento(email, dto);

        return ResponseEntity.ok("¡Entrenamiento registrado con éxito! Tus músculos te lo agradecen.");
    }

    @GetMapping("/historial")
    public ResponseEntity<List<HistorialEntrenamientoDTO>> obtenerHistorial(Authentication authentication) {
        // Obtenemos quién es el usuario a través de su token
        String email = authentication.getName();

        // Pedimos su historial al servicio
        List<HistorialEntrenamientoDTO> historial = entrenamientoService.obtenerHistorialUsuario(email);

        return ResponseEntity.ok(historial);
    }

    @GetMapping("/1rm/{ejercicioId}")
    public ResponseEntity<FuerzaMaximaDTO> calcularFuerzaMaxima(
            Authentication authentication,
            @PathVariable Long ejercicioId) {
        // Obtenemos el email del usuario
        String email = authentication.getName();

        // Llamamos al servicio para calcular el 1RM
        FuerzaMaximaDTO resultado = entrenamientoService.calcular1RM(email, ejercicioId);

        return ResponseEntity.ok(resultado);
    }

    // 🔥 NUEVO: Endpoint para ELIMINAR un registro del diario
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarRegistro(
            Authentication authentication,
            @PathVariable Long id) {
        String email = authentication.getName();
        entrenamientoService.eliminarRegistro(email, id);
        return ResponseEntity.ok("Registro eliminado correctamente.");
    }

    // 🔥 NUEVO: Endpoint para EDITAR peso y repeticiones en el diario
    @PutMapping("/{id}")
    public ResponseEntity<String> editarRegistro(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam Double peso,
            @RequestParam Integer reps) {
        String email = authentication.getName();
        entrenamientoService.editarRegistro(email, id, peso, reps);
        return ResponseEntity.ok("Registro actualizado correctamente.");
    }
}