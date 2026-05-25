package com.fitness.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private String objetivo;
    private String nivelExperiencia;
    private String tipoSuscripcion;
    private Integer edad;
    private Double peso;
    private Double altura;
    private String sexo;
    private Integer diasDisponibles;
}