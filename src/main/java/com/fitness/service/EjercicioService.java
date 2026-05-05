package com.fitness.service;

import com.fitness.dto.EjercicioDTO;
import com.fitness.model.Ejercicio;
import com.fitness.repository.EjercicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;

    // Obtiene todos los ejercicios para mostrarlos en la tabla del Admin
    public List<EjercicioDTO> obtenerTodos() {
        return ejercicioRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Guarda un nuevo ejercicio en la base de datos
    public EjercicioDTO crearEjercicio(EjercicioDTO dto) {
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setNombre(dto.getNombre());
        ejercicio.setDescripcion(dto.getDescripcion());
        ejercicio.setTipoEjercicio(dto.getTipoEjercicio());
        ejercicio.setDificultad(dto.getDificultad());
        ejercicio.setGrupoMuscular(dto.getGrupoMuscular());
        ejercicio.setUrlVideo(dto.getUrlVideo());
        ejercicio.setActivo(true); // Siempre activo por defecto al crearlo

        Ejercicio guardado = ejercicioRepository.save(ejercicio);
        return mapToDTO(guardado);
    }

    // Borra un ejercicio por su ID
    public void eliminarEjercicio(Long id) {
        if (!ejercicioRepository.existsById(id)) {
            throw new RuntimeException("Ejercicio no encontrado con ID: " + id);
        }
        ejercicioRepository.deleteById(id);
    }

    // Transformador manual de Entidad a DTO (Buena práctica)
    private EjercicioDTO mapToDTO(Ejercicio ejercicio) {
        EjercicioDTO dto = new EjercicioDTO();
        dto.setId(ejercicio.getId());
        dto.setNombre(ejercicio.getNombre());
        dto.setDescripcion(ejercicio.getDescripcion());
        dto.setTipoEjercicio(ejercicio.getTipoEjercicio());
        dto.setDificultad(ejercicio.getDificultad());
        dto.setGrupoMuscular(ejercicio.getGrupoMuscular());
        dto.setUrlVideo(ejercicio.getUrlVideo());
        dto.setActivo(ejercicio.getActivo());
        return dto;
    }
}