package com.fitness.service;

import com.fitness.dto.EntrenamientoRequestDTO;
import com.fitness.dto.FuerzaMaximaDTO;
import com.fitness.dto.HistorialEntrenamientoDTO;
import com.fitness.dto.RegistroEjercicioRequestDTO;
import com.fitness.exception.ResourceNotFoundException;
import com.fitness.model.EntrenamientoRealizado;
import com.fitness.model.Rutina;
import com.fitness.model.RutinaEjercicio;
import com.fitness.model.Usuario;
import com.fitness.repository.EntrenamientoRealizadoRepository;
import com.fitness.repository.RutinaEjercicioRepository;
import com.fitness.repository.RutinaRepository;
import com.fitness.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntrenamientoService {

    private final EntrenamientoRealizadoRepository entrenamientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RutinaRepository rutinaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;

    @Transactional
    public void guardarEntrenamiento(String emailUsuario, EntrenamientoRequestDTO dto) {

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Rutina rutina = rutinaRepository.findById(dto.getRutinaId())
                .orElseThrow(() -> new ResourceNotFoundException("Rutina no encontrada"));

        LocalDate fechaHoy = LocalDate.now();

        if (dto.getRegistros() != null) {
            for (RegistroEjercicioRequestDTO regDTO : dto.getRegistros()) {

                RutinaEjercicio rutinaEjercicio = rutinaEjercicioRepository.findById(regDTO.getEjercicioId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ejercicio de rutina no encontrado"));

                EntrenamientoRealizado registro = new EntrenamientoRealizado();
                registro.setUsuario(usuario);
                registro.setRutina(rutina);
                registro.setRutinaEjercicio(rutinaEjercicio);
                registro.setFechaEntrenamiento(fechaHoy);

                registro.setSeriesRealizadas(regDTO.getSeriesCompletadas());
                registro.setRepeticionesRealizadas(regDTO.getRepeticionesRealizadas());
                registro.setPesoUtilizado(regDTO.getPesoLevantadoKg());
                registro.setTiempoSesionMinutos(dto.getDuracionMinutos());
                registro.setNotas(dto.getNotas());
                registro.setCompletado(true);

                registro.setSemanaEntrenamiento(dto.getSemana() != null ? dto.getSemana() : 1);

                entrenamientoRepository.save(registro);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<HistorialEntrenamientoDTO> obtenerHistorialUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<EntrenamientoRealizado> historial = entrenamientoRepository
                .findByUsuarioIdOrderByFechaEntrenamientoDesc(usuario.getId());

        return historial.stream().map(h -> {
            HistorialEntrenamientoDTO dto = new HistorialEntrenamientoDTO();
            dto.setId(h.getId());
            dto.setNombreRutina(h.getRutina().getNombre());
            dto.setNombreEjercicio(h.getRutinaEjercicio().getEjercicio().getNombre());
            dto.setGrupoMuscular(h.getRutinaEjercicio().getEjercicio().getGrupoMuscular());
            dto.setFecha(h.getFechaEntrenamiento());
            dto.setSeriesRealizadas(h.getSeriesRealizadas());
            dto.setRepeticionesRealizadas(h.getRepeticionesRealizadas());
            dto.setPesoUtilizado(h.getPesoUtilizado());
            dto.setTiempoSesionMinutos(h.getTiempoSesionMinutos());

            dto.setSemanaRutina(h.getSemanaEntrenamiento() != null ? h.getSemanaEntrenamiento() : 1);

            if (h.getRutinaEjercicio() != null) {
                dto.setDiaRutina("Día " + h.getRutinaEjercicio().getDiaSemana());
            } else {
                dto.setDiaRutina("Manual");
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FuerzaMaximaDTO calcular1RM(String emailUsuario, Long rutinaEjercicioId) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<EntrenamientoRealizado> historial = entrenamientoRepository
                .findByUsuarioIdOrderByFechaEntrenamientoDesc(usuario.getId());

        List<EntrenamientoRealizado> historialDelEjercicio = historial.stream()
                .filter(h -> h.getRutinaEjercicio().getId().equals(rutinaEjercicioId))
                .collect(Collectors.toList());

        if (historialDelEjercicio.isEmpty()) {
            throw new IllegalArgumentException("Aún no tienes registros con peso para este ejercicio.");
        }

        EntrenamientoRealizado mejorLevantamiento = historialDelEjercicio.stream()
                .max(Comparator.comparingDouble(h -> h.getPesoUtilizado() * h.getRepeticionesRealizadas()))
                .orElseThrow(() -> new IllegalArgumentException("Error al calcular el mejor levantamiento."));

        double peso = mejorLevantamiento.getPesoUtilizado();
        int reps = mejorLevantamiento.getRepeticionesRealizadas();

        double fuerzaMaxima = peso * (1.0 + (reps / 30.0));
        fuerzaMaxima = Math.round(fuerzaMaxima * 100.0) / 100.0;

        FuerzaMaximaDTO dto = new FuerzaMaximaDTO();
        dto.setNombreEjercicio(mejorLevantamiento.getRutinaEjercicio().getEjercicio().getNombre());
        dto.setPesoMaximoEstimado1RM(fuerzaMaxima);

        if (fuerzaMaxima >= 100) {
            dto.setMensajeMotivacional("¡Eres una bestia! Has superado la barrera de los 100kg teóricos.");
        } else {
            dto.setMensajeMotivacional("¡Gran trabajo! Sigue entrenando para romper tu propio récord.");
        }

        return dto;
    }

    @Transactional
    public void eliminarRegistro(String emailUsuario, Long registroId) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        EntrenamientoRealizado registro = entrenamientoRepository.findById(registroId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado"));

        if (!registro.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para eliminar este registro");
        }

        entrenamientoRepository.delete(registro);
    }

    @Transactional
    public void editarRegistro(String emailUsuario, Long registroId, Double nuevoPeso, Integer nuevasReps) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        EntrenamientoRealizado registro = entrenamientoRepository.findById(registroId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado"));

        if (!registro.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para editar este registro");
        }

        registro.setPesoUtilizado(nuevoPeso);
        registro.setRepeticionesRealizadas(nuevasReps);
        entrenamientoRepository.save(registro);
    }
}