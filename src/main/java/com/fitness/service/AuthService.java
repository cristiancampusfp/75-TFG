package com.fitness.service;

import com.fitness.dto.AuthResponse;
import com.fitness.dto.LoginRequest;
import com.fitness.dto.RegisterRequest;
import com.fitness.dto.UsuarioDTO;
import com.fitness.model.Rol;
import com.fitness.model.Usuario;
import com.fitness.repository.RolRepository;
import com.fitness.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void registrarUsuario(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(request.getNombre());
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));

        nuevoUsuario.setEdad(request.getEdad());
        nuevoUsuario.setPeso(request.getPeso());
        nuevoUsuario.setAltura(request.getAltura());
        // 🔥 AQUÍ ESTÁ LA MAGIA: Guardamos el sexo en el usuario
        nuevoUsuario.setSexo(request.getSexo());
        nuevoUsuario.setObjetivo(request.getObjetivo());
        nuevoUsuario.setNivelExperiencia(request.getNivelExperiencia());
        nuevoUsuario.setDiasDisponibles(request.getDiasDisponibles());

        nuevoUsuario.setTipoSuscripcion("gratuita");
        nuevoUsuario.setActivo(true);

        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Error: Rol CLIENTE no encontrado en la base de datos."));
        nuevoUsuario.setRol(rolCliente);

        usuarioRepository.save(nuevoUsuario);
    }

    public AuthResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().getNombre());

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol().getNombre());
        dto.setObjetivo(usuario.getObjetivo());
        dto.setNivelExperiencia(usuario.getNivelExperiencia());
        dto.setTipoSuscripcion(usuario.getTipoSuscripcion());

        return new AuthResponse(token, dto);
    }
}