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

    @Scheduled(fixedRate = 60000)
    public void actualizarEstadosServicios() {
        logger.info("Iniciando actualización automática de estados de servicios...");

        LocalDate hoy = LocalDate.now();

        List<Servicio> serviciosHoy = servicioRepository.findByEstadoAndFechaBetween(
                Servicio.EstadoServicio.PROGRAMADO,
                hoy,
                hoy
        );

        List<Servicio> serviciosAnteriores = servicioRepository.findByEstadoAndFechaBefore(
                Servicio.EstadoServicio.PROGRAMADO,
                hoy
        );

        logger.info("Encontrados {} servicios programados para hoy.", serviciosHoy.size());
        logger.info("Encontrados {} servicios programados para días anteriores.", serviciosAnteriores.size());

        int serviciosActualizados = 0;

        for (Servicio servicio : serviciosHoy) {
            LocalTime horaServicio = servicio.getHora();
            if (horaServicio == null) {
                logger.warn("Servicio ID: {} tiene hora nula, omitiendo.", servicio.getIdServicio());
                continue;
            }
            LocalDateTime horaServicioHoy = hoy.atTime(horaServicio);
            LocalDateTime ahora = LocalDateTime.now();

            if (ahora.isAfter(horaServicioHoy) || ahora.equals(horaServicioHoy)) {
                int filasActualizadas = servicioRepository.actualizarEstado(servicio.getIdServicio(), Servicio.EstadoServicio.EN_PROGRESO);
                if (filasActualizadas > 0) {
                    logger.info("Servicio ID: {} cambiado a 'EN_PROGRESO'.", servicio.getIdServicio());
                    serviciosActualizados++;
                }
            }
        }

        for (Servicio servicio : serviciosAnteriores) {
            int filasActualizadas = servicioRepository.actualizarEstado(servicio.getIdServicio(), Servicio.EstadoServicio.COMPLETADO);
            if (filasActualizadas > 0) {
                logger.info("Servicio ID: {} cambiado a 'COMPLETADO'.", servicio.getIdServicio());
                serviciosActualizados++;
            }
        }

        logger.info("Actualización finalizada. {} servicios actualizados.", serviciosActualizados);
    }
}