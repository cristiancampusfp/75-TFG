package com.fitness.repository;

import com.fitness.model.Dieta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DietaRepository extends JpaRepository<Dieta, Long> {
    // Para recuperar la dieta actual del usuario
    Optional<Dieta> findByUsuarioId(Long usuarioId);
}