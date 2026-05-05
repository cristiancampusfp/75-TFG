package com.fitness.repository;

import com.fitness.model.RegistroPeso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroPesoRepository extends JpaRepository<RegistroPeso, Long> {

    // 🔥 CRÍTICO: Este método trae los 2 pesos más recientes para comparar el progreso en el auto-ajuste
    List<RegistroPeso> findTop2ByUsuarioIdOrderByFechaDesc(Long usuarioId);

    // 🔥 NUEVO: Trae todo el historial ordenado del más nuevo al más viejo (para la lista visual)
    List<RegistroPeso> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    // Por si en el futuro quieres pintar una gráfica de progreso (del más viejo al más nuevo)
    List<RegistroPeso> findByUsuarioIdOrderByFechaAsc(Long usuarioId);
}