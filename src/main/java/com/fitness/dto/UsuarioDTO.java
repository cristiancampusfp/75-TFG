package com.fitness.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private String objetivo;
    private String nivelExperiencia;
    private String tipoSuscripcion;
}