package com.fitness.repository;

import com.fitness.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {
    List<Ejercicio> findByActivoTrue();
    List<Ejercicio> findByDificultad(String dificultad);
}