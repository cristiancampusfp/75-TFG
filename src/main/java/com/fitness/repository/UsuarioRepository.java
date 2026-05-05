package com.fitness.repository;

import com.fitness.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Para cuando implementemos el Login
    Optional<Usuario> findByEmail(String email);

    // Para comprobar que nadie se registre dos veces con el mismo correo
    boolean existsByEmail(String email);
}