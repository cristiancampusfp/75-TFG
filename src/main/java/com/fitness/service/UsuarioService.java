package com.fitness.service;

import com.fitness.dto.UsuarioDTO;
import com.fitness.model.Rol;
import com.fitness.model.Usuario;
import com.fitness.repository.RolRepository;
import com.fitness.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;       // 🔥 NUEVO: Para buscar y asignar el nuevo rol
    private final PasswordEncoder passwordEncoder;   // 🔥 NUEVO: Para encriptar la contraseña si se cambia

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

    // 4. 🔥 ACTUALIZADO - Método para el admin: Actualizar un usuario CON PODER TOTAL
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO dtoActualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 4.1 Actualización de campos básicos
        if (dtoActualizado.getNombre() != null) usuario.setNombre(dtoActualizado.getNombre());
        if (dtoActualizado.getEmail() != null) usuario.setEmail(dtoActualizado.getEmail());
        if (dtoActualizado.getObjetivo() != null) usuario.setObjetivo(dtoActualizado.getObjetivo());
        if (dtoActualizado.getNivelExperiencia() != null) usuario.setNivelExperiencia(dtoActualizado.getNivelExperiencia());

        // 4.2 Actualización de Contraseña (Solo si el admin ha escrito algo)
        if (dtoActualizado.getPassword() != null && !dtoActualizado.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(dtoActualizado.getPassword()));
        }

        // 4.3 Actualización de Rol (Si el admin lo ha cambiado)
        if (dtoActualizado.getRol() != null) {
            Rol nuevoRol = rolRepository.findByNombre(dtoActualizado.getRol())
                    .orElseThrow(() -> new RuntimeException("Error: El rol " + dtoActualizado.getRol() + " no existe en la base de datos"));
            usuario.setRol(nuevoRol);
        }

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