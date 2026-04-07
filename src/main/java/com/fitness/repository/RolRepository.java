package com.fitness.repository;

import com.fitness.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    // Necesitamos este método para buscar el rol "CLIENTE" cuando alguien se registre
    Optional<Rol> findByNombre(String nombre);
}