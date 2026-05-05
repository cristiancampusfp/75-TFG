package com.fitness.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rutina_ejercicios", uniqueConstraints = {
        // 🔥 ACTUALIZADO: Ahora la combinación única incluye la 'semana'
        @UniqueConstraint(name = "unique_rutina_semana_dia_orden", columnNames = {"rutina_id", "semana", "dia_semana", "orden"})
})
public class RutinaEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rutina_id", nullable = false)
    private Rutina rutina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejercicio_id", nullable = false)
    private Ejercicio ejercicio;

    // --- NUEVO CAMPO SEMANA ---
    @Column(name = "semana", nullable = false)
    private Integer semana = 1;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false)
    private Integer series;

    // --- CAMBIADO A STRING PARA LOS RANGOS (Ej: "5-8") ---
    @Column(nullable = false, length = 20)
    private String repeticiones;

    @Column(name = "descanso_segundos", nullable = false)
    private Integer descansoSegundos;

    @Column(name = "peso_recomendado", columnDefinition = "DECIMAL(5,2)")
    private Double pesoRecomendado;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}