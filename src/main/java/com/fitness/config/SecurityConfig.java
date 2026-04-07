package com.fitness.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Este Bean es el que usa AuthService para encriptar la contraseña (passwordEncoder.encode)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desactivamos CSRF temporalmente para facilitar las pruebas del MVP
                .authorizeHttpRequests(auth -> auth
                        // Permitimos acceso libre a las rutas de login y registro (tanto las vistas HTML como la API)
                        .requestMatchers("/api/auth/**", "/register", "/login", "/css/**", "/js/**").permitAll()
                        // Cualquier otra ruta requerirá estar autenticado
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}