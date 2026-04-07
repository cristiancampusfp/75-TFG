package com.fitness.controller;

import com.fitness.dto.UsuarioDTO;
import com.fitness.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UsuarioService usuarioService;

    // 1. Endpoint para el cliente: Ver su propio perfil
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getMiPerfil(Authentication authentication) {
        // authentication.getName() saca el email del token de forma segura
        UsuarioDTO perfil = usuarioService.obtenerPerfil(authentication.getName());
        return ResponseEntity.ok(perfil);
    }

    // 2. Endpoint para el Admin: Ver todos los usuarios (READ del CRUD)
    @GetMapping("/todos")
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        // AQUÍ ESTABA EL ERROR: Ahora llamamos al método con el nombre correcto
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // 3. Endpoint para el Admin: Borrar un usuario por ID (DELETE del CRUD)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}