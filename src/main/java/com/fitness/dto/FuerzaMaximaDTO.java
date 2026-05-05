package com.fitness.dto;

import lombok.Data;

@Data
public class FuerzaMaximaDTO {
    private String nombreEjercicio;
    private Double pesoMaximoEstimado1RM;
    private String mensajeMotivacional;
}