package com.fitness.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 14, message = "Debes tener al menos 14 años para registrarte")
    @Max(value = 100, message = "Edad no válida")
    private Integer edad;

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor a 0")
    private Double peso;

    @NotNull(message = "La altura es obligatoria")
    @Positive(message = "La altura debe ser mayor a 0")
    private Double altura;

    @NotBlank(message = "El sexo es obligatorio para los cálculos metabólicos")
    private String sexo;

    @NotBlank(message = "El objetivo es obligatorio")
    private String objetivo;

    @NotBlank(message = "El nivel de experiencia es obligatorio")
    private String nivelExperiencia;

    @NotNull(message = "Los días disponibles son obligatorios")
    @Min(value = 1, message = "Debes tener al menos 1 día disponible")
    @Max(value = 7, message = "El máximo son 7 días a la semana")
    private Integer diasDisponibles;
}