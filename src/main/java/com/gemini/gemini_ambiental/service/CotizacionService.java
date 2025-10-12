package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.Cotizacion;
import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.CotizacionRepository;
import com.gemini.gemini_ambiental.repository.PersonaRepository;
import com.gemini.gemini_ambiental.dto.CotizacionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CotizacionService {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private PersonaRepository personaRepository;

    public CotizacionDTO createCotizacion(CotizacionDTO cotizacionDTO) {
        Cotizacion cotizacion = convertToEntity(cotizacionDTO);
        Cotizacion savedCotizacion = cotizacionRepository.save(cotizacion);
        return convertToDTO(savedCotizacion);
    }

    public CotizacionDTO updateCotizacion(String id, CotizacionDTO cotizacionDTO) {
        Cotizacion existingCotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        // Actualizar campos
        existingCotizacion.setEstado(Cotizacion.EstadoCotizacion.valueOf(cotizacionDTO.getEstado()));
        existingCotizacion.setFechaPreferida(cotizacionDTO.getFechaPreferida());
        existingCotizacion.setFechaRespuesta(cotizacionDTO.getFechaRespuesta());
        existingCotizacion.setPrioridad(cotizacionDTO.getPrioridad());
        existingCotizacion.setDescripcionProblema(cotizacionDTO.getDescripcionProblema());
        existingCotizacion.setNotasInternas(cotizacionDTO.getNotasInternas());
        existingCotizacion.setCostoTotalCotizacion(cotizacionDTO.getCostoTotalCotizacion());

        Cotizacion updatedCotizacion = cotizacionRepository.save(existingCotizacion);
        return convertToDTO(updatedCotizacion);
    }

    public void deleteCotizacion(String id) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        cotizacionRepository.delete(cotizacion);
    }

    public CotizacionDTO getCotizacionById(String id) {
        Cotizacion cotizacion = cotizacionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));
        return convertToDTO(cotizacion);
    }

    public List<CotizacionDTO> getAllCotizaciones() {
        return cotizacionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CotizacionDTO> searchCotizaciones(String fechaInicio, String fechaFin, String estado, String dniCliente, String dniEmpleado) {
        // Convertir Strings a LocalDateTime
        LocalDateTime start = fechaInicio != null ? LocalDateTime.parse(fechaInicio) : null;
        LocalDateTime end = fechaFin != null ? LocalDateTime.parse(fechaFin) : null;

        List<Cotizacion> cotizaciones = cotizacionRepository.findAll();

        if (start != null && end != null) {
            cotizaciones = cotizaciones.stream()
                    .filter(c -> !c.getFechaSolicitud().isBefore(start) && !c.getFechaSolicitud().isAfter(end))
                    .collect(Collectors.toList());
        }

        if (estado != null) {
            Cotizacion.EstadoCotizacion estadoEnum = Cotizacion.EstadoCotizacion.valueOf(estado);
            cotizaciones = cotizaciones.stream()
                    .filter(c -> c.getEstado() == estadoEnum)
                    .collect(Collectors.toList());
        }

        if (dniCliente != null) {
            cotizaciones = cotizaciones.stream()
                    .filter(c -> c.getCliente() != null && c.getCliente().getDni().equals(dniCliente))
                    .collect(Collectors.toList());
        }

        if (dniEmpleado != null) {
            cotizaciones = cotizaciones.stream()
                    .filter(c -> c.getEmpleado() != null && c.getEmpleado().getDni().equals(dniEmpleado))
                    .collect(Collectors.toList());
        }

        return cotizaciones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Cotizacion convertToEntity(CotizacionDTO dto) {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setIdCotizacion(dto.getIdCotizacion());
        // Convertir String a EstadoCotizacion
        cotizacion.setEstado(Cotizacion.EstadoCotizacion.valueOf(dto.getEstado()));
        // Convertir LocalDateTime a LocalDate si es necesario
        cotizacion.setFechaSolicitud(dto.getFechaSolicitud());        cotizacion.setFechaPreferida(dto.getFechaPreferida());
        cotizacion.setFechaRespuesta(dto.getFechaRespuesta());
        cotizacion.setPrioridad(dto.getPrioridad());
        cotizacion.setDescripcionProblema(dto.getDescripcionProblema());
        cotizacion.setNotasInternas(dto.getNotasInternas());
        cotizacion.setCostoTotalCotizacion(dto.getCostoTotalCotizacion());

        // Asignar relaciones
        if (dto.getDniCliente() != null) {
            Persona cliente = personaRepository.findByDni(dto.getDniCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con DNI: " + dto.getDniCliente()));
            cotizacion.setCliente(cliente);
        }

        if (dto.getDniEmpleado() != null) {
            Persona empleado = personaRepository.findByDni(dto.getDniEmpleado())
                    .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con DNI: " + dto.getDniEmpleado()));
            cotizacion.setEmpleado(empleado);
        }

        return cotizacion;
    }

    private CotizacionDTO convertToDTO(Cotizacion cotizacion) {
        CotizacionDTO dto = new CotizacionDTO();
        dto.setIdCotizacion(cotizacion.getIdCotizacion());
        dto.setDniCliente(cotizacion.getCliente() != null ? cotizacion.getCliente().getDni() : null);
        dto.setDniEmpleado(cotizacion.getEmpleado() != null ? cotizacion.getEmpleado().getDni() : null);
        // Convertir EstadoCotizacion a String
        dto.setEstado(cotizacion.getEstado().toString());
        // ✅ CORRECCIÓN 1: Asignar directamente LocalDateTime (no convertir LocalDate)
        dto.setFechaSolicitud(cotizacion.getFechaSolicitud()); // Ya es LocalDateTime
        dto.setFechaPreferida(cotizacion.getFechaPreferida()); // Es LocalDate, el DTO debe aceptar LocalDate o convertir
        dto.setFechaRespuesta(cotizacion.getFechaRespuesta()); // Es LocalDateTime
        dto.setPrioridad(cotizacion.getPrioridad());
        dto.setDescripcionProblema(cotizacion.getDescripcionProblema());
        dto.setNotasInternas(cotizacion.getNotasInternas());
        dto.setCostoTotalCotizacion(cotizacion.getCostoTotalCotizacion());
        dto.setFechaCreacion(cotizacion.getFechaCreacion()); // Ya es LocalDateTime

        // Agregar datos adicionales para la UI
        if (cotizacion.getCliente() != null) {
            dto.setNombreCliente(cotizacion.getCliente().getNombre());
            dto.setTelefonoCliente(cotizacion.getCliente().getTelefono());
            dto.setCorreoCliente(cotizacion.getCliente().getCorreo());
        }

        if (cotizacion.getEmpleado() != null) {
            dto.setNombreEmpleado(cotizacion.getEmpleado().getNombre());
            dto.setTelefonoEmpleado(cotizacion.getEmpleado().getTelefono());
            dto.setCorreoEmpleado(cotizacion.getEmpleado().getCorreo());
        }

        return dto;
    }
}