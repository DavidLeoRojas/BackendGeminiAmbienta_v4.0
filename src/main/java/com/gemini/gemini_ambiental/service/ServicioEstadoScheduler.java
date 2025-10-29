package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.Servicio;
import com.gemini.gemini_ambiental.repository.ServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;

@Service
@Transactional
public class ServicioEstadoScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ServicioEstadoScheduler.class);

    @Autowired
    private ServicioRepository servicioRepository;

    /**
     * Tarea programada que se ejecuta cada minuto.
     * Verifica los servicios con estado 'Programado' y cambia su estado si corresponde.
     */
    @Scheduled(fixedRate = 60000) // 60000 milisegundos = 1 minuto
    public void actualizarEstadosServicios() {
        logger.info("Iniciando actualización automática de estados de servicios...");

        LocalDate hoy = LocalDate.now();
        LocalDate ayer = hoy.minusDays(1); // Fecha de ayer

        // --- 1. Buscar servicios programados para HOY ---
        List<Servicio> serviciosHoy = servicioRepository.findByEstadoAndFechaBetween(
                Servicio.EstadoServicio.Programado, // <-- Usar el nombre correcto del enum
                hoy, // <-- LocalDate
                hoy  // <-- LocalDate
        );

        logger.info("Encontrados {} servicios programados para hoy ({}).", serviciosHoy.size(), hoy);

        // --- 2. Buscar servicios programados para DIAS ANTERIORES ---
        List<Servicio> serviciosAnteriores = servicioRepository.findByEstadoAndFechaBefore(
                Servicio.EstadoServicio.Programado, // <-- Usar el nombre correcto del enum
                hoy // <-- LocalDate: buscar servicios antes de hoy (excluyendo hoy)
        );

        logger.info("Encontrados {} servicios programados para días anteriores (antes de {}).", serviciosAnteriores.size(), hoy);

        int serviciosActualizados = 0;

        // --- 3. Procesar servicios de hoy ---
        for (Servicio servicio : serviciosHoy) {
            // Verificar si la hora actual es mayor o igual a la hora del servicio
            // Esto implica que el servicio "debería haber empezado"
            LocalTime horaServicio = servicio.getHora(); // Devuelve LocalTime
            if (horaServicio == null) {
                logger.warn("Servicio ID: {} tiene hora nula, omitiendo.", servicio.getIdServicio());
                continue; // O manejar este caso como prefieras
            }
            LocalDateTime horaServicioHoy = hoy.atTime(horaServicio); // Fecha de hoy + hora del servicio
            LocalDateTime ahora = LocalDateTime.now();

            if (ahora.isAfter(horaServicioHoy) || ahora.equals(horaServicioHoy)) {
                // Cambiar estado a 'En Progreso'
                // ✅ USAR EL MÉTODO ESPECÍFICO PARA ACTUALIZAR SOLO EL ESTADO
                int filasActualizadas = servicioRepository.actualizarEstado(servicio.getIdServicio(), Servicio.EstadoServicio.EnProgreso);
                if (filasActualizadas > 0) {
                    logger.info("Servicio ID: {} cambiado a 'En Progreso'. Fecha: {}, Hora: {}", servicio.getIdServicio(), servicio.getFecha(), servicio.getHora());
                    serviciosActualizados++;
                } else {
                    logger.warn("No se actualizó el estado para el servicio ID: {} (posiblemente ya no existe o no cumple condiciones).", servicio.getIdServicio());
                }
            }
        }

        // --- 4. Procesar servicios anteriores ---
        // --- 4. Procesar servicios anteriores ---
        for (Servicio servicio : serviciosAnteriores) {
            // Si está en estado 'Programado' y la fecha ya pasó, cambiar a 'Completado'
            // Opcional: podrías verificar si la hora del día de la fecha pasada también ya pasó
            // Para simplificar, asumiremos que si es un día anterior, y llegó aquí, debe finalizarse
            // ✅ USAR EL MÉTODO ESPECÍFICO PARA ACTUALIZAR SOLO EL ESTADO
            int filasActualizadas = servicioRepository.actualizarEstado(servicio.getIdServicio(), Servicio.EstadoServicio.Completado);
            if (filasActualizadas > 0) {
                logger.info("Servicio ID: {} (fecha: {}) cambiado a 'Completado' por haber pasado la fecha.", servicio.getIdServicio(), servicio.getFecha());
                serviciosActualizados++; // ✅ CORREGIDO: Nombre de variable
            } else {
                logger.warn("No se actualizó el estado para el servicio ID: {} (posiblemente ya no existe o no cumple condiciones).", servicio.getIdServicio());
            }
        }

        logger.info("Actualización automática finalizada. {} servicios actualizados.", serviciosActualizados);
    }
}