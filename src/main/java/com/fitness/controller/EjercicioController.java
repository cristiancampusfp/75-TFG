package com.fitness.controller;

import com.fitness.dto.EjercicioDTO;
import com.fitness.service.EjercicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ejercicios")
@RequiredArgsConstructor
public class EjercicioController {

    private final EjercicioService ejercicioService;

    // GET: Cualquier usuario logueado (Admin o Cliente) puede ver el catálogo
    @GetMapping
    public ResponseEntity<List<EjercicioDTO>> listarEjercicios() {
        return ResponseEntity.ok(ejercicioService.obtenerTodos());
    }

    // POST: SOLO el ADMIN puede crear ejercicios nuevos
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EjercicioDTO> crearEjercicio(@RequestBody EjercicioDTO ejercicioDTO) {
        EjercicioDTO nuevoEjercicio = ejercicioService.crearEjercicio(ejercicioDTO);
        return new ResponseEntity<>(nuevoEjercicio, HttpStatus.CREATED);
    }

    // DELETE: SOLO el ADMIN puede borrar ejercicios
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarEjercicio(@PathVariable Long id) {
        ejercicioService.eliminarEjercicio(id);
        return ResponseEntity.noContent().build();
    }
}