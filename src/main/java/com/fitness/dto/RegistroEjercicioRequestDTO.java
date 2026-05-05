package com.fitness.dto;

import lombok.Data;

@Data
public class RegistroEjercicioRequestDTO {
    private Long ejercicioId;
    private Integer seriesCompletadas;
    private Integer repeticionesRealizadas;
    private Double pesoLevantadoKg;
}