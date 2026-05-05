package com.fitness.service;

import com.fitness.dto.DietaRequestDTO;
import com.fitness.model.*;
import com.fitness.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DietaService {

    private final DietaRepository dietaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RegistroPesoRepository registroPesoRepository;
    private final IAService iaService;

    /**
     * Calcula el TDEE y los macros iniciales basándose en Mifflin-St Jeor (ajustado por sexo)
     * y el reparto de macros de Eric Helms.
     */
    @Transactional
    public Dieta calcularYGuardarPlan(String email, DietaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Mifflin-St Jeor: (10 * peso) + (6.25 * altura_cm) - (5 * edad) + s
        // Convertimos altura (metros en BBDD) a centímetros (para la fórmula)
        double alturaCm = usuario.getAltura() * 100;
        double bmr = (10 * usuario.getPeso()) + (6.25 * alturaCm) - (5 * usuario.getEdad());

        // Ajuste 's' según sexo del Usuario
        if ("MASCULINO".equalsIgnoreCase(usuario.getSexo())) {
            bmr += 5;
        } else {
            bmr -= 161;
        }

        double factorActividad = switch (dto.getNivelActividad()) {
            case "sedentario" -> 1.2;
            case "ligero" -> 1.375;
            case "moderado" -> 1.55;
            case "intenso" -> 1.725;
            default -> 1.375;
        };

        int mantenimiento = (int) (bmr * factorActividad);

        // 2. Ajuste por Objetivo (Ganar Masa Muscular +300 / Perder -500)
        int objetivoCals = mantenimiento;
        if ("GANAR_MUSCULO".equalsIgnoreCase(usuario.getObjetivo())) {
            objetivoCals += 300;
        } else if ("PERDER_GRASA".equalsIgnoreCase(usuario.getObjetivo())) {
            objetivoCals -= 500;
        }

        // 3. Reparto Macros (Filosofía Eric Helms)
        int pro = (int) (usuario.getPeso() * 2.0); // 2g/kg
        int fat = (int) (usuario.getPeso() * 0.8); // 0.8g/kg
        int calsRestantes = objetivoCals - (pro * 4) - (fat * 9);
        int carb = calsRestantes / 4;

        // 4. Guardar o actualizar la dieta del usuario
        Dieta dieta = dietaRepository.findByUsuarioId(usuario.getId()).orElse(new Dieta());
        dieta.setUsuario(usuario);
        dieta.setAltura(dto.getAltura());
        dieta.setNivelActividad(dto.getNivelActividad());
        dieta.setMantenimientoCalorias(mantenimiento);
        dieta.setObjetivoCalorias(objetivoCals);
        dieta.setProteinas(pro);
        dieta.setGrasas(fat);
        dieta.setCarbohidratos(carb);
        dieta.setAlimentosGusta(dto.getAlimentosGusta());
        dieta.setAlimentosOdia(dto.getAlimentosOdia());

        return dietaRepository.save(dieta);
    }

    /**
     * Recupera la dieta actual para mostrarla en el panel.
     */
    @Transactional(readOnly = true)
    public Dieta obtenerDietaPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return dietaRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Aún no tienes un plan nutricional configurado"));
    }

    /**
     * Llama a la IA para generar un menú diario detallado basado en los macros calculados.
     */
    @Transactional
    public String generarMenuConIA(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Dieta dieta = dietaRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Primero debes configurar tu plan nutricional"));

        // Construcción del Prompt Profesional
        String prompt = String.format(
                "Actúa como un nutricionista deportivo de élite. Diseña un menú diario basado en la ciencia de Eric Helms.\n\n" +
                        "DATOS DEL CLIENTE:\n" +
                        "- Objetivo calórico: %d kcal\n" +
                        "- Macros: Proteína %dg, Carbohidratos %dg, Grasas %dg\n" +
                        "- Alimentos favoritos: %s\n" +
                        "- Alimentos a evitar: %s\n\n" +
                        "REGLAS DEL MENÚ:\n" +
                        "1. Divide el total en 4 comidas (Desayuno, Almuerzo, Merienda, Cena).\n" +
                        "2. Indica cantidades exactas en gramos para cada alimento.\n" +
                        "3. Asegúrate de que la suma total de macros coincida con el objetivo mencionado.\n" +
                        "4. Usa un tono motivador y profesional.\n\n" +
                        "Responde directamente con el menú detallado sin introducciones innecesarias.",
                dieta.getObjetivoCalorias(),
                dieta.getProteinas(),
                dieta.getCarbohidratos(),
                dieta.getGrasas(),
                dieta.getAlimentosGusta(),
                dieta.getAlimentosOdia()
        );

        // Llamada al servicio de IA
        String menuGenerado = iaService.pedirRutinaAGemini(prompt);

        // Persistencia: Guardamos el menú para no tener que generarlo cada vez
        dieta.setMenuSemanal(menuGenerado);
        dietaRepository.save(dieta);

        return menuGenerado;
    }

    /**
     * Registra el peso semanal y activa el ajuste automático de calorías si el progreso se estanca.
     */
    @Transactional
    public void registrarPesoSemanal(String email, Double nuevoPeso) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        RegistroPeso registro = new RegistroPeso();
        registro.setUsuario(usuario);
        registro.setPeso(nuevoPeso);
        registro.setFecha(LocalDate.now());
        registroPesoRepository.save(registro);

        // Actualizamos el peso actual del usuario para futuros cálculos
        usuario.setPeso(nuevoPeso);
        usuarioRepository.save(usuario);

        // Analizar si el plan necesita ajustes
        revisarProgreso(usuario);
    }

    /**
     * 🔥 NUEVO: Recupera el historial completo de pesos del usuario ordenado del más reciente al más antiguo.
     */
    @Transactional(readOnly = true)
    public List<RegistroPeso> obtenerHistorialPesos(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        return registroPesoRepository.findByUsuarioIdOrderByFechaDesc(usuario.getId());
    }

    /**
     * Lógica de auto-ajuste: Si el peso no sube en volumen, aumenta carbohidratos.
     */
    private void revisarProgreso(Usuario usuario) {
        List<RegistroPeso> historial = registroPesoRepository.findTop2ByUsuarioIdOrderByFechaDesc(usuario.getId());

        if (historial.size() == 2 && "GANAR_MUSCULO".equalsIgnoreCase(usuario.getObjetivo())) {
            double pesoActual = historial.get(0).getPeso();
            double pesoAnterior = historial.get(1).getPeso();

            // Si el peso se ha mantenido o ha bajado durante el superávit
            if (pesoActual <= pesoAnterior) {
                Dieta dieta = dietaRepository.findByUsuarioId(usuario.getId()).orElseThrow();

                // Incremento conservador: +150 kcal procedentes de carbohidratos (~37g)
                dieta.setObjetivoCalorias(dieta.getObjetivoCalorias() + 150);
                dieta.setCarbohidratos(dieta.getCarbohidratos() + 37);

                dietaRepository.save(dieta);
            }
        }
    }
}