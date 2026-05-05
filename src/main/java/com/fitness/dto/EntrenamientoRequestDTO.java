package com.fitness.dto;

import lombok.Data;
import java.util.List;

@Data
public class EntrenamientoRequestDTO {
    private Long rutinaId;

    // 🔥 NUEVO: Recibe la semana que el usuario selecciona en el modal
    private Integer semana;

    private Integer duracionMinutos;
    private String notas;
    private List<RegistroEjercicioRequestDTO> registros;
}