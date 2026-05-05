package com.fitness.repository;

import com.fitness.model.RutinaEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Long> {

    // 🔥 ACTUALIZADO: Ahora ordena primero por SEMANA, luego por DÍA, y finalmente por ORDEN
    List<RutinaEjercicio> findByRutinaIdOrderBySemanaAscDiaSemanaAscOrdenAsc(Long rutinaId);
}