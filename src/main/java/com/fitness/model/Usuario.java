package com.fitness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    private Integer edad;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double peso;

    @Column(columnDefinition = "DECIMAL(3,2)")
    private Double altura;

    @Column(length = 50)
    private String objetivo;

    @Column(name = "nivel_experiencia", length = 50)
    private String nivelExperiencia;

    @Column(name = "dias_disponibles")
    private Integer diasDisponibles;

    @Column(length = 10)
    private String sexo;

    @Column(length = 20)
    private String tipoSuscripcion;

    @Column(columnDefinition = "boolean default true")
    private Boolean activo = true;

    private LocalDateTime fechaRegistro;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ==========================================================
    // 🔥 RELACIONES CON CASCADA PARA EL BORRADO Y @JsonIgnore 🔥
    // ==========================================================

    @JsonIgnore // Ignorado en el JSON para que no de error de bucle
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rutina> rutinas = new ArrayList<>();

    @JsonIgnore // Ignorado en el JSON para que no de error de bucle
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Dieta dieta;

    @JsonIgnore // Ignorado en el JSON para que no de error de bucle
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroPeso> historialPesos = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
    }
}