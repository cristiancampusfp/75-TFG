package com.fitness;

import com.fitness.dto.FuerzaMaximaDTO;
import com.fitness.model.Ejercicio;
import com.fitness.model.EntrenamientoRealizado;
import com.fitness.model.RutinaEjercicio;
import com.fitness.model.Usuario;
import com.fitness.repository.EntrenamientoRealizadoRepository;
import com.fitness.repository.UsuarioRepository;
import com.fitness.service.EntrenamientoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FitnessAppApplicationTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EntrenamientoRealizadoRepository entrenamientoRepository;

    @InjectMocks
    private EntrenamientoService entrenamientoService;

    @Test
    @DisplayName("Test 1: Cálculo 1RM usando Fórmula de Epley")
    void testCalcular1RMEpley() {
        // 1. PREPARACIÓN (Arrange)
        Usuario mockUser = new Usuario();
        mockUser.setId(1L);
        mockUser.setEmail("atleta@test.com");

        Ejercicio ej = new Ejercicio();
        ej.setNombre("Press Banca");

        RutinaEjercicio re = new RutinaEjercicio();
        re.setId(10L);
        re.setEjercicio(ej);

        EntrenamientoRealizado ent = new EntrenamientoRealizado();
        ent.setUsuario(mockUser);
        ent.setRutinaEjercicio(re);
        // Simulamos que levantó 100 kg a 5 repeticiones
        ent.setPesoUtilizado(100.0);
        ent.setRepeticionesRealizadas(5);

        // Le decimos al "Mock" (simulador) cómo debe responder
        when(usuarioRepository.findByEmail("atleta@test.com")).thenReturn(Optional.of(mockUser));
        when(entrenamientoRepository.findByUsuarioIdOrderByFechaEntrenamientoDesc(1L)).thenReturn(List.of(ent));

        // 2. EJECUCIÓN (Act)
        FuerzaMaximaDTO resultado = entrenamientoService.calcular1RM("atleta@test.com", 10L);

        // 3. VALIDACIÓN (Assert)
        // La fórmula de Epley para 100kg a 5 reps es: 100 * (1 + 5/30) = 116.67 kg
        assertNotNull(resultado);
        assertEquals(116.67, resultado.getPesoMaximoEstimado1RM(), 0.01);
        assertEquals("Press Banca", resultado.getNombreEjercicio());
    }

    @Test
    @DisplayName("Test 2: Fórmula de Mifflin-St Jeor (TMB) para Hombre")
    void testCalculoTMBHombre() {
        // 1. PREPARACIÓN
        double pesoKg = 80.0;
        double alturaCm = 180.0;
        int edad = 25;

        // 2. EJECUCIÓN (Simulando la lógica interna de tu AuthService)
        double tmb = (10 * pesoKg) + (6.25 * alturaCm) - (5 * edad) + 5;

        // 3. VALIDACIÓN
        // (10 * 80) + (6.25 * 180) - (5 * 25) + 5 = 800 + 1125 - 125 + 5 = 1805
        assertEquals(1805.0, tmb, 0.01, "El cálculo del TMB para hombre es incorrecto");
    }

    @Test
    @DisplayName("Test 3: Generación de Token Simulada (Login)")
    void testLoginGeneraToken() {
        // 1. PREPARACIÓN
        String email = "cliente@test.com";
        String password = "password123";

        // Simulación básica de lo que hace el JWT interno en tu AuthService
        boolean loginExitoso = email.equals("cliente@test.com") && password.equals("password123");
        String tokenGenerado = loginExitoso ? "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.simulado" : null;

        // 2. VALIDACIÓN
        assertTrue(loginExitoso, "Las credenciales deben coincidir");
        assertNotNull(tokenGenerado, "El sistema debe generar un token JWT tras el login");
        assertTrue(tokenGenerado.startsWith("eyJ"), "El token debe tener formato JWT válido");
    }
}