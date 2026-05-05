package com.fitness.repository;

import com.fitness.model.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    // La que ya tenías para buscar todas las rutinas
    List<Rutina> findByUsuarioId(Long usuarioId);

    // NUEVA LÍNEA: Para buscar las rutinas filtrando por su estado (ACTIVA)
    List<Rutina> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}