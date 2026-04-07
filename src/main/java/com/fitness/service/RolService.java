package com.fitness.service;

import com.fitness.model.Rol;
import com.fitness.repository.RolRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Slf4j
@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    /**
     * Obtiene un rol por su nombre
     */
    public Optional<Rol> obtenerPorNombre(String nombre) {
        log.info("🔍 Buscando rol: {}", nombre);
        return rolRepository.findByNombre(nombre);
    }

    /**
     * Obtiene un rol por su ID
     */
    public Optional<Rol> obtenerPorId(Long id) {
        log.info("🔍 Buscando rol con ID: {}", id);
        return rolRepository.findById(id);
    }

    /**
     * Obtiene todos los roles
     */
    public List<Rol> obtenerTodos() {
        log.info("📋 Obteniendo todos los roles");
        return rolRepository.findAll();
    }

    /**
     * Crea un nuevo rol
     */
    public Rol crearRol(Rol rol) {
        log.info("💾 Creando rol: {}", rol.getNombre());

        if (rolRepository.findByNombre(rol.getNombre()).isPresent()) {
            log.warn("⚠️ Rol ya existe: {}", rol.getNombre());
            throw new RuntimeException("El rol ya existe");
        }

        Rol rolCreado = rolRepository.save(rol);
        log.info("✅ Rol creado exitosamente: {}", rolCreado.getNombre());
        return rolCreado;
    }

    /**
     * Actualiza un rol existente
     */
    public Rol actualizarRol(Long id, Rol rolActualizado) {
        log.info("🔄 Actualizando rol con ID: {}", id);

        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Rol no encontrado: {}", id);
                    return new RuntimeException("Rol no encontrado");
                });

        if (rolActualizado.getNombre() != null) {
            rol.setNombre(rolActualizado.getNombre());
        }
        if (rolActualizado.getDescripcion() != null) {
            rol.setDescripcion(rolActualizado.getDescripcion());
        }

        Rol rolGuardado = rolRepository.save(rol);
        log.info("✅ Rol actualizado: {}", rolGuardado.getNombre());
        return rolGuardado;
    }

    /**
     * Elimina un rol
     */
    public void eliminarRol(Long id) {
        log.info("🗑️ Eliminando rol con ID: {}", id);

        if (!rolRepository.existsById(id)) {
            log.error("❌ Rol no encontrado: {}", id);
            throw new RuntimeException("Rol no encontrado");
        }

        rolRepository.deleteById(id);
        log.info("✅ Rol eliminado correctamente");
    }

    /**
     * Obtiene el rol CLIENTE
     */
    public Optional<Rol> obtenerRolCliente() {
        log.info("🔍 Obteniendo rol CLIENTE");
        return rolRepository.findByNombre("CLIENTE");
    }

    /**
     * Obtiene el rol ADMIN
     */
    public Optional<Rol> obtenerRolAdmin() {
        log.info("🔍 Obteniendo rol ADMIN");
        return rolRepository.findByNombre("ADMIN");
    }
}