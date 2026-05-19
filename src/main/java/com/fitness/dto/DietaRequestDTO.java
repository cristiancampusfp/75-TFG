package com.fitness.dto;

import lombok.Data;

@Data
public class DietaRequestDTO {
    private Double altura;
    private Double pesoActual;
    private String objetivo;
    private String nivelActividad;
    private String alimentosGusta;
    private String alimentosOdia;
}