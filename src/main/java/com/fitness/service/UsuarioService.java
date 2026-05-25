package com.fitness.service;

import com.fitness.dto.UsuarioDTO;
import com.fitness.model.Rol;
import com.fitness.model.Usuario;
import com.fitness.repository.RolRepository;
import com.fitness.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. Método para el cliente: Obtener su propio perfil
    public UsuarioDTO obtenerPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapearADTO(usuario);
    }

    // 2. Método para el admin: Obtener usuarios PAGINADOS
    public Page<UsuarioDTO> obtenerTodosLosUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(this::mapearADTO);
    }

    // 3. Método para el admin: Eliminar un usuario por su ID
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    // 4. Método para el admin: Actualizar un usuario CON PODER TOTAL
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO dtoActualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 4.1 Actualización de campos básicos
        if (dtoActualizado.getNombre() != null) usuario.setNombre(dtoActualizado.getNombre());
        if (dtoActualizado.getEmail() != null) usuario.setEmail(dtoActualizado.getEmail());
        if (dtoActualizado.getObjetivo() != null) usuario.setObjetivo(dtoActualizado.getObjetivo());
        if (dtoActualizado.getNivelExperiencia() != null) usuario.setNivelExperiencia(dtoActualizado.getNivelExperiencia());

        // 🔥 NUEVO: Actualización de datos biométricos y de configuración
        if (dtoActualizado.getEdad() != null) usuario.setEdad(dtoActualizado.getEdad());
        if (dtoActualizado.getPeso() != null) usuario.setPeso(dtoActualizado.getPeso());
        if (dtoActualizado.getAltura() != null) usuario.setAltura(dtoActualizado.getAltura());
        if (dtoActualizado.getSexo() != null) usuario.setSexo(dtoActualizado.getSexo());
        if (dtoActualizado.getDiasDisponibles() != null) usuario.setDiasDisponibles(dtoActualizado.getDiasDisponibles());

        // 4.2 Actualización de Contraseña
        if (dtoActualizado.getPassword() != null && !dtoActualizado.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(dtoActualizado.getPassword()));
        }

        // 4.3 Actualización de Rol
        if (dtoActualizado.getRol() != null) {
            Rol nuevoRol = rolRepository.findByNombre(dtoActualizado.getRol())
                    .orElseThrow(() -> new RuntimeException("Error: El rol " + dtoActualizado.getRol() + " no existe en la BD"));
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

        // 🔥 NUEVO: Empaquetar datos biométricos para mandarlos al Frontend
        dto.setEdad(usuario.getEdad());
        dto.setPeso(usuario.getPeso());
        dto.setAltura(usuario.getAltura());
        dto.setSexo(usuario.getSexo());
        dto.setDiasDisponibles(usuario.getDiasDisponibles());

        return dto;
    }
}