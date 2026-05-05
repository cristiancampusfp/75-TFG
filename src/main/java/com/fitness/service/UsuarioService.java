package com.fitness.service;

import com.fitness.dto.UsuarioDTO;
import com.fitness.model.Usuario;
import com.fitness.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // 1. Método para el cliente: Obtener su propio perfil
    public UsuarioDTO obtenerPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapearADTO(usuario);
    }

    // 2. Método para el admin: Obtener la lista de todos los usuarios (READ del CRUD)
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    // 3. Método para el admin: Eliminar un usuario por su ID (DELETE del CRUD)
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    // 4. NUEVO - Método para el admin: Actualizar un usuario (UPDATE del CRUD)
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO dtoActualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Actualizamos solo los datos de perfil (protegemos email, password y rol)
        if (dtoActualizado.getNombre() != null) usuario.setNombre(dtoActualizado.getNombre());
        if (dtoActualizado.getObjetivo() != null) usuario.setObjetivo(dtoActualizado.getObjetivo());
        if (dtoActualizado.getNivelExperiencia() != null) usuario.setNivelExperiencia(dtoActualizado.getNivelExperiencia());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return mapearADTO(usuarioGuardado);
    }

    // Método privado de utilidad para convertir la Entidad a DTO
    private UsuarioDTO mapearADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());

        if (usuario.getRol() != null) {
            dto.setRol(usuario.getRol().getNombre());
        }

        dto.setObjetivo(usuario.getObjetivo());
        dto.setNivelExperiencia(usuario.getNivelExperiencia());
        dto.setTipoSuscripcion(usuario.getTipoSuscripcion());

        return dto;
    }
}