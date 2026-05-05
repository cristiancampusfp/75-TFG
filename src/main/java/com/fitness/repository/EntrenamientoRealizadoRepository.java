package com.fitness.repository;

import com.fitness.model.EntrenamientoRealizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrenamientoRealizadoRepository extends JpaRepository<EntrenamientoRealizado, Long> {
    // Para sacar el historial de un usuario
    List<EntrenamientoRealizado> findByUsuarioIdOrderByFechaEntrenamientoDesc(Long usuarioId);
}