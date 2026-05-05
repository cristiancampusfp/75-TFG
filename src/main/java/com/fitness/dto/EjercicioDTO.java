package com.fitness.dto;

import lombok.Data;

@Data
public class EjercicioDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String tipoEjercicio;
    private String dificultad;
    private String grupoMuscular;
    private String urlVideo;
    private Boolean activo;
}