package com.fitness.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private Integer edad;
    private Double peso;
    private Double altura;
    private String objetivo; // 'ganar_musculo', 'perder_peso', etc.
    private String nivelExperiencia; // 'principiante', 'intermedio', etc.
    private Integer diasDisponibles;
}