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
        @UniqueConstraint(name = "unique_rutina_dia_orden", columnNames = {"rutina_id", "dia_semana", "orden"})
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

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false)
    private Integer repeticiones;

    @Column(name = "descanso_segundos", nullable = false)
    private Integer descansoSegundos;

    @Column(name = "peso_recomendado", columnDefinition = "DECIMAL(5,2)")
    private Double pesoRecomendado;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}