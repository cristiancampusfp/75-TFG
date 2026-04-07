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

    // 1. Método para el cliente: Obtener su propio perfil buscando por el email del token
    public UsuarioDTO obtenerPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapearADTO(usuario);
    }

    // 2. Método para el admin: Obtener la lista de todos los usuarios de la base de datos (READ del CRUD)
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

    // Método privado de utilidad para convertir la Entidad (con contraseña) a DTO (seguro para la web)
    private UsuarioDTO mapearADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());

        // Protegemos la aplicación asegurándonos de que el rol no sea nulo al mapear
        if (usuario.getRol() != null) {
            dto.setRol(usuario.getRol().getNombre());
        }

        dto.setObjetivo(usuario.getObjetivo());
        dto.setNivelExperiencia(usuario.getNivelExperiencia());
        dto.setTipoSuscripcion(usuario.getTipoSuscripcion());

        return dto;
    }
}