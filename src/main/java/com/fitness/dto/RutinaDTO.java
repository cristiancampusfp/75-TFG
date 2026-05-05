package com.fitness.dto;

import lombok.Data;
import java.util.List;

@Data
public class RutinaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String intensidad;
    private Integer diasSemana;
    private List<RutinaEjercicioDTO> ejercicios; // ¡Aquí va la lista de ejercicios de la rutina!
}