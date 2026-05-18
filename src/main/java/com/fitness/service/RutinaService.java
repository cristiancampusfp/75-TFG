package com.fitness.service;

import com.fitness.dto.RutinaDTO;
import com.fitness.dto.RutinaEjercicioDTO;
import com.fitness.model.Ejercicio;
import com.fitness.model.Rutina;
import com.fitness.model.RutinaEjercicio;
import com.fitness.model.Usuario;
import com.fitness.repository.EjercicioRepository;
import com.fitness.repository.RutinaEjercicioRepository;
import com.fitness.repository.RutinaRepository;
import com.fitness.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;
    private final EjercicioRepository ejercicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final IAService iaService;

    @Transactional(readOnly = true)
    public List<RutinaDTO> obtenerRutinasDeUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Rutina> rutinas = rutinaRepository.findByUsuarioIdAndEstado(usuario.getId(), "ACTIVA");
        return rutinas.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public RutinaDTO generarRutinaAuto(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Rutina> rutinasActivas = rutinaRepository.findByUsuarioIdAndEstado(usuario.getId(), "ACTIVA");
        for (Rutina r : rutinasActivas) {
            r.setEstado("ARCHIVADA");
            rutinaRepository.save(r);
        }

        List<Ejercicio> catalogo = ejercicioRepository.findByActivoTrue();
        if (catalogo.isEmpty()) {
            throw new RuntimeException("No hay ejercicios en el catálogo.");
        }

        String listaEjercicios = catalogo.stream()
                .map(e -> "- " + e.getNombre() + " (Músculo: " + e.getGrupoMuscular() + ")")
                .collect(Collectors.joining("\n"));

        int dias = usuario.getDiasDisponibles() != null ? usuario.getDiasDisponibles() : 3;
        String objetivo = usuario.getObjetivo() != null ? usuario.getObjetivo() : "Ganancia muscular";
        String nivel = usuario.getNivelExperiencia() != null ? usuario.getNivelExperiencia() : "Intermedio";

        // 🔥 PROMPT ACTUALIZADO: Instrucciones estrictas de generar UNA SOLA SEMANA
        String prompt = "Actúa como un entrenador personal experto en Powerbuilding y Biomecánica. " +
                "Genera UN MICROCICLO BASE (1 SOLA SEMANA) para un usuario nivel " + nivel + " con objetivo: " + objetivo + " entrenando " + dias + " días a la semana.\n\n" +
                "REGLAS DE ORO:\n" +
                "1. SOBRECARGA PROGRESIVA: El usuario repetirá esta única semana durante todo su mesociclo. POR TANTO, GENERA SOLO LOS EJERCICIOS DE LA SEMANA 1. El campo 'semana' SIEMPRE DEBE SER 1.\n" +
                "2. SELECCIÓN DE EJERCICIOS: Usa los mejores ejercicios posibles (barras, mancuernas, poleas, máquinas) según la ciencia.\n" +
                "3. ESTRUCTURA: 1-2 BÁSICOS pesados, 2-3 COMPLEMENTARIOS, 1-2 AISLAMIENTO.\n" +
                "4. RANGOS DE REPETICIONES (OBLIGATORIO):\n" +
                "   - BÁSICOS: Usa rangos de fuerza como '5-8' o '3-5'.\n" +
                "   - COMPLEMENTARIOS: Usa '8-10' o '10-12'.\n" +
                "   - AISLAMIENTO: Usa '12-15' o '15-20'.\n" +
                "5. REGLA ESTRICTA DE FORMATO: Responde EXCLUSIVAMENTE con el JSON. NO digas 'Hola', NO digas 'Aquí tienes tu rutina'. SOLO EL JSON puro que empiece por la llave { y termine por la llave }.\n\n" +
                "ESTRUCTURA JSON OBLIGATORIA:\n" +
                "{\"ejercicios\": [{\"nombre\": \"Nombre Exacto\", \"grupoMuscular\": \"Pecho\", \"semana\": 1, \"dia\": 1, \"orden\": 1, \"series\": 3, \"repsRango\": \"5-8\", \"descanso\": 180}]}";

        String respuestaIA = iaService.pedirRutinaAGemini(prompt);

        Rutina rutina = new Rutina();
        rutina.setUsuario(usuario);
        rutina.setNombre("Rutina Base PRO: " + objetivo);
        rutina.setDescripcion("Microciclo base optimizado. Repite esta rutina semanalmente aplicando sobrecarga progresiva en tu diario de entrenamientos.");
        rutina.setDiasSemana(dias);
        rutina.setIntensidad(calcularIntensidad(nivel));
        rutina.setVolumenSemanal(dias * 5);
        rutina.setFechaGeneracion(LocalDateTime.now());
        rutina.setEstado("ACTIVA");

        Rutina rutinaGuardada = rutinaRepository.save(rutina);

        parsearYGuardarEjercicios(respuestaIA, rutinaGuardada, catalogo);

        return mapToDTO(rutinaGuardada);
    }

    private void parsearYGuardarEjercicios(String jsonIA, Rutina rutina, List<Ejercicio> catalogo) {
        try {
            // EXTRACCIÓN INTELIGENTE
            String jsonLimpio = jsonIA;
            int startIndex = jsonLimpio.indexOf("{");
            int endIndex = jsonLimpio.lastIndexOf("}");

            if (startIndex != -1 && endIndex != -1) {
                jsonLimpio = jsonLimpio.substring(startIndex, endIndex + 1);
            }

            JSONObject obj = new JSONObject(jsonLimpio);
            JSONArray array = obj.getJSONArray("ejercicios");

            for (int i = 0; i < array.length(); i++) {
                JSONObject ejIA = array.getJSONObject(i);
                String nombreIA = ejIA.getString("nombre");

                Ejercicio ejBD = catalogo.stream()
                        .filter(e -> e.getNombre().equalsIgnoreCase(nombreIA))
                        .findFirst()
                        .orElse(null);

                // PROTECCIÓN DEL CATÁLOGO (Requisito de David)
                if (ejBD == null) {
                    ejBD = new Ejercicio();
                    ejBD.setNombre(nombreIA);
                    ejBD.setGrupoMuscular(ejIA.has("grupoMuscular") ? ejIA.getString("grupoMuscular") : "General");
                    ejBD.setActivo(false); // No lo mostramos en el catálogo oficial
                    ejBD.setPendienteRevision(true); // Marca de agua de la IA
                    ejBD = ejercicioRepository.save(ejBD);
                }

                RutinaEjercicio re = new RutinaEjercicio();
                re.setRutina(rutina);
                re.setEjercicio(ejBD);

                // 🔥 CANDADO DE SEGURIDAD: Forzamos a que siempre se guarde como Semana 1
                re.setSemana(1);

                re.setDiaSemana(ejIA.has("dia") ? ejIA.getInt("dia") : 1);
                re.setOrden(ejIA.has("orden") ? ejIA.getInt("orden") : (i + 1));
                re.setSeries(ejIA.has("series") ? ejIA.getInt("series") : 3);
                re.setDescansoSegundos(ejIA.has("descanso") ? ejIA.getInt("descanso") : 90);
                re.setPesoRecomendado(0.0);

                // LÓGICA DE RANGOS A PRUEBA DE ERRORES:
                String rangoRaw = "";
                if (ejIA.has("repsRango")) {
                    rangoRaw = String.valueOf(ejIA.get("repsRango"));
                } else if (ejIA.has("repeticiones")) {
                    rangoRaw = String.valueOf(ejIA.get("repeticiones"));
                }

                // Si la IA manda un número suelto, lo convertimos a rango manualmente
                if (rangoRaw.matches("\\d+")) {
                    int n = Integer.parseInt(rangoRaw);
                    if (n <= 5) rangoRaw = "3-5";
                    else if (n <= 8) rangoRaw = "5-8";
                    else if (n <= 12) rangoRaw = "10-12";
                    else rangoRaw = "12-15";
                } else if (rangoRaw.isEmpty()) {
                    rangoRaw = "8-12";
                }

                re.setRepeticiones(rangoRaw);

                rutinaEjercicioRepository.save(re);
            }
        } catch (Exception e) {
            System.err.println("Error parseando JSON: " + e.getMessage());
            throw new RuntimeException("Error en el formato de la IA. Por favor, reintenta.");
        }
    }

    private String calcularIntensidad(String nivel) {
        if (nivel == null) return "BAJA";
        if ("Avanzado".equalsIgnoreCase(nivel)) return "ALTA";
        if ("Intermedio".equalsIgnoreCase(nivel)) return "MEDIA";
        return "BAJA";
    }

    private RutinaDTO mapToDTO(Rutina rutina) {
        RutinaDTO dto = new RutinaDTO();
        dto.setId(rutina.getId());
        dto.setNombre(rutina.getNombre());
        dto.setDescripcion(rutina.getDescripcion());
        dto.setIntensidad(rutina.getIntensidad());
        dto.setDiasSemana(rutina.getDiasSemana());

        List<RutinaEjercicio> ejercicios = rutinaEjercicioRepository
                .findByRutinaIdOrderBySemanaAscDiaSemanaAscOrdenAsc(rutina.getId());

        dto.setEjercicios(ejercicios.stream().map(this::mapEjercicioToDTO).collect(Collectors.toList()));
        return dto;
    }

    private RutinaEjercicioDTO mapEjercicioToDTO(RutinaEjercicio re) {
        RutinaEjercicioDTO dto = new RutinaEjercicioDTO();
        dto.setId(re.getId());
        dto.setNombreEjercicio(re.getEjercicio().getNombre());
        dto.setGrupoMuscular(re.getEjercicio().getGrupoMuscular());
        dto.setSemana(re.getSemana());
        dto.setDiaSemana(re.getDiaSemana());
        dto.setNombreDia(obtenerNombreDia(re.getDiaSemana(), re.getRutina().getDiasSemana()));
        dto.setOrden(re.getOrden());
        dto.setSeries(re.getSeries());
        dto.setRepeticiones(re.getRepeticiones());
        dto.setDescansoSegundos(re.getDescansoSegundos());
        return dto;
    }

    private String obtenerNombreDia(int dia, int totalDias) {
        if (totalDias >= 5) {
            return switch (dia) {
                case 1 -> "Lunes";
                case 2 -> "Martes";
                case 3 -> "Miércoles";
                case 4 -> "Viernes";
                case 5 -> "Sábado";
                default -> "Día " + dia;
            };
        } else if (totalDias == 4) {
            return switch (dia) {
                case 1 -> "Lunes";
                case 2 -> "Martes";
                case 3 -> "Jueves";
                case 4 -> "Viernes";
                default -> "Día " + dia;
            };
        } else if (totalDias == 3) {
            return switch (dia) {
                case 1 -> "Lunes";
                case 2 -> "Miércoles";
                case 3 -> "Viernes";
                default -> "Día " + dia;
            };
        }
        return "Día " + dia;
    }
}