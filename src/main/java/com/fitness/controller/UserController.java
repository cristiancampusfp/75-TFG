package com.fitness.controller;

import com.fitness.dto.UsuarioDTO;
import com.fitness.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // Rutas genéricas para separar cliente y admin
@RequiredArgsConstructor
public class UserController {

    private final UsuarioService usuarioService;

    // 1. Endpoint para el cliente: Ver su propio perfil
    @GetMapping("/users/me")
    public ResponseEntity<UsuarioDTO> getMiPerfil(Authentication authentication) {
        // authentication.getName() saca el email del token de forma segura
        UsuarioDTO perfil = usuarioService.obtenerPerfil(authentication.getName());
        return ResponseEntity.ok(perfil);
    }

    // ==========================================
    // ZONA DE ADMINISTRADOR (Blindaje de Seguridad)
    // ==========================================

    // 2. Endpoint para el Admin: Ver todos los usuarios paginados (READ)
    @PreAuthorize("hasAuthority('ADMIN')") // <-- Bloquea a cualquiera que no sea ADMIN
    @GetMapping("/admin/usuarios")
    public ResponseEntity<Page<UsuarioDTO>> obtenerTodos(
            @PageableDefault(size = 10, page = 0) Pageable pageable) { // Por defecto 10 usuarios por página

        // Ahora le pasamos el objeto pageable al servicio
        Page<UsuarioDTO> usuariosPaginados = usuarioService.obtenerTodosLosUsuarios(pageable);
        return ResponseEntity.ok(usuariosPaginados);
    }

    // 3. Endpoint para el Admin: Borrar un usuario por ID (DELETE)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/admin/usuarios/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. NUEVO - Endpoint para el Admin: Editar un usuario (UPDATE)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/admin/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        try {
            UsuarioDTO actualizado = usuarioService.actualizarUsuario(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}