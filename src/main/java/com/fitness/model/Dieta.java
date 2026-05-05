package com.fitness.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "dietas")
public class Dieta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private Double altura;
    private String nivelActividad; // sedentario, ligero, moderado, intenso

    // Calorías y Macros finales
    private Integer mantenimientoCalorias;
    private Integer objetivoCalorias;
    private Integer proteinas;
    private Integer carbohidratos;
    private Integer grasas;

    // Preferencias (Para la IA)
    @Column(columnDefinition = "TEXT")
    private String alimentosGusta;

    @Column(columnDefinition = "TEXT")
    private String alimentosOdia;

    @Column(columnDefinition = "TEXT")
    private String menuSemanal; // El texto que generará Gemini
}