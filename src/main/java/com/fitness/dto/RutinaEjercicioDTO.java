package com.fitness.dto;

import lombok.Data;

@Data
public class RutinaEjercicioDTO {
    private Long id;
    private String nombreEjercicio; // Solo mandamos el nombre, no todo el objeto Ejercicio
    private String grupoMuscular;

    // --- NUEVO: Para saber a qué semana pertenece el ejercicio ---
    private Integer semana;

    private Integer diaSemana;
    private String nombreDia; // Aquí guardaremos "Lunes", "Martes", etc.
    private Integer orden;
    private Integer series;

    // --- ACTUALIZADO: Cambiado a String para aceptar rangos como "5-8" o "10-12" ---
    private String repeticiones;

    private Integer descansoSegundos;
}