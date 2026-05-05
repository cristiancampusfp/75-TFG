package com.fitness.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "entrenamientos_realizados")
public class EntrenamientoRealizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rutina_id", nullable = false)
    private Rutina rutina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rutina_ejercicio_id", nullable = false)
    private RutinaEjercicio rutinaEjercicio;

    @Column(name = "fecha_entrenamiento", nullable = false)
    private LocalDate fechaEntrenamiento;

    @Column(name = "series_realizadas")
    private Integer seriesRealizadas;

    @Column(name = "repeticiones_realizadas")
    private Integer repeticionesRealizadas;

    @Column(name = "peso_utilizado")
    private Double pesoUtilizado;

    @Column(name = "tiempo_sesion_minutos")
    private Integer tiempoSesionMinutos;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(columnDefinition = "boolean default false")
    private Boolean completado = false;

    // 🔥 NUEVO: Aquí guardaremos la semana que el usuario elija a mano
    @Column(name = "semana_entrenamiento")
    private Integer semanaEntrenamiento;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}