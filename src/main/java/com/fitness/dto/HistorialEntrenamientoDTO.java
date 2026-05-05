package com.fitness.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HistorialEntrenamientoDTO {
    private Long id;
    private String nombreRutina;
    private String nombreEjercicio;
    private String grupoMuscular;
    private LocalDate fecha;
    private Integer seriesRealizadas;
    private Integer repeticionesRealizadas;
    private Double pesoUtilizado;
    private Integer tiempoSesionMinutos;


    private Integer semanaRutina;
    private String diaRutina;
}