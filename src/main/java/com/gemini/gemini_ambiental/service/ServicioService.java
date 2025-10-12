package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.dto.ServicioDTO;
import com.gemini.gemini_ambiental.entity.Cotizacion;
import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.entity.Servicio;
import com.gemini.gemini_ambiental.entity.TipoServicio;
import com.gemini.gemini_ambiental.repository.CotizacionRepository;
import com.gemini.gemini_ambiental.repository.PersonaRepository;
import com.gemini.gemini_ambiental.repository.ServicioRepository;
import com.gemini.gemini_ambiental.repository.TipoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    public List<ServicioDTO> getAllServicios() {
        List<Servicio> servicios = servicioRepository.findAll();
        return servicios.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ServicioDTO> searchServicios(String fecha, String estado, String dniEmpleado, String dniCliente) {
        // Implementa la lógica de búsqueda según tus necesidades
        // Por ahora, devuelve todos
        return getAllServicios();
    }

    public ServicioDTO getServicioById(String id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
        return convertToDTO(servicio);
    }

    public ServicioDTO createServicio(ServicioDTO servicioDTO) {
        Servicio servicio = new Servicio();

        // No asignar idServicio aquí, JPA lo generará automáticamente
        // servicio.setIdServicio(UUID.randomUUID().toString()); // <-- REMOVIDO

        // Asignar otros campos desde el DTO
        servicio.setFecha(servicioDTO.getFecha());
        servicio.setHora(servicioDTO.getHora());
        servicio.setEstado(mapStringToEstado(servicioDTO.getEstado())); // Convertir String a Enum
        servicio.setObservaciones(servicioDTO.getObservaciones());
        servicio.setPrioridad(servicioDTO.getPrioridad());
        servicio.setDuracionEstimada(servicioDTO.getDuracionEstimada());
        servicio.setServicioSinCotizacion(servicioDTO.getServicioSinCotizacion());

        // Buscar y asignar entidades relacionadas
        if (servicioDTO.getIdCotizacion() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(servicioDTO.getIdCotizacion())
                    .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + servicioDTO.getIdCotizacion()));
            servicio.setCotizacion(cotizacion);
        }

        if (servicioDTO.getDniCliente() != null) {
            Persona cliente = personaRepository.findByDni(servicioDTO.getDniCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con DNI: " + servicioDTO.getDniCliente()));
            servicio.setCliente(cliente);
        }

        if (servicioDTO.getDniEmpleadoAsignado() != null) {
            Persona tecnico = personaRepository.findByDni(servicioDTO.getDniEmpleadoAsignado())
                    .orElseThrow(() -> new RuntimeException("Técnico no encontrado con DNI: " + servicioDTO.getDniEmpleadoAsignado()));
            servicio.setEmpleadoAsignado(tecnico);
        }

        if (servicioDTO.getIdTipoServicio() != null) {
            TipoServicio tipoServ = tipoServicioRepository.findById(servicioDTO.getIdTipoServicio())
                    .orElseThrow(() -> new RuntimeException("Tipo de Servicio no encontrado con ID: " + servicioDTO.getIdTipoServicio()));
            servicio.setTipoServicio(tipoServ);
        }

        Servicio savedServicio = servicioRepository.save(servicio);
        return convertToDTO(savedServicio); // El DTO ahora contendrá el ID generado por JPA
    }

    public ServicioDTO updateServicio(String id, ServicioDTO servicioDTO) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));

        // Actualizar campos básicos
        servicio.setFecha(servicioDTO.getFecha());
        servicio.setHora(servicioDTO.getHora());
        servicio.setEstado(mapStringToEstado(servicioDTO.getEstado())); // Convertir String a Enum
        servicio.setObservaciones(servicioDTO.getObservaciones());
        servicio.setPrioridad(servicioDTO.getPrioridad());
        servicio.setDuracionEstimada(servicioDTO.getDuracionEstimada());

        // Actualizar entidades relacionadas
        if (servicioDTO.getIdCotizacion() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(servicioDTO.getIdCotizacion())
                    .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + servicioDTO.getIdCotizacion()));
            servicio.setCotizacion(cotizacion);
        } else {
            servicio.setCotizacion(null); // Si el ID es null, desvincular
        }

        if (servicioDTO.getDniCliente() != null) {
            Persona cliente = personaRepository.findByDni(servicioDTO.getDniCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con DNI: " + servicioDTO.getDniCliente()));
            servicio.setCliente(cliente);
        } else {
            servicio.setCliente(null); // Si el DNI es null, desvincular
        }

        if (servicioDTO.getDniEmpleadoAsignado() != null) {
            Persona tecnico = personaRepository.findByDni(servicioDTO.getDniEmpleadoAsignado())
                    .orElseThrow(() -> new RuntimeException("Técnico no encontrado con DNI: " + servicioDTO.getDniEmpleadoAsignado()));
            servicio.setEmpleadoAsignado(tecnico);
        } else {
            servicio.setEmpleadoAsignado(null); // Si el DNI es null, desvincular
        }

        if (servicioDTO.getIdTipoServicio() != null) {
            TipoServicio tipoServ = tipoServicioRepository.findById(servicioDTO.getIdTipoServicio())
                    .orElseThrow(() -> new RuntimeException("Tipo de Servicio no encontrado con ID: " + servicioDTO.getIdTipoServicio()));
            servicio.setTipoServicio(tipoServ);
        } else {
            servicio.setTipoServicio(null); // Si el ID es null, desvincular
        }

        Servicio updatedServicio = servicioRepository.save(servicio);
        return convertToDTO(updatedServicio);
    }

    public void deleteServicio(String id) {
        if (!servicioRepository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado con ID: " + id);
        }
        servicioRepository.deleteById(id);
    }

    private Servicio.EstadoServicio mapStringToEstado(String estadoStr) {
        if (estadoStr == null) {
            return Servicio.EstadoServicio.Programado; // Valor por defecto
        }
        // Reemplazar guiones bajos por espacios para revertir el cambio del frontend
        String estadoNormalizado = estadoStr.replace('_', ' ');
        try {
            return Servicio.EstadoServicio.valueOf(estadoNormalizado);
        } catch (IllegalArgumentException e) {
            // Si no coincide con ningún valor del enum, usar uno por defecto o lanzar error
            System.err.println("Estado desconocido: " + estadoStr + ", usando Programado por defecto.");
            return Servicio.EstadoServicio.Programado;
        }
    }

    private ServicioDTO convertToDTO(Servicio servicio) {
        ServicioDTO dto = new ServicioDTO();
        dto.setIdServicio(servicio.getIdServicio()); // JPA ya asignó el ID
        dto.setIdCotizacion(servicio.getCotizacion() != null ? servicio.getCotizacion().getIdCotizacion() : null);
        dto.setDniCliente(servicio.getCliente() != null ? servicio.getCliente().getDni() : null);
        dto.setDniEmpleadoAsignado(servicio.getEmpleadoAsignado() != null ? servicio.getEmpleadoAsignado().getDni() : null);
        dto.setIdTipoServicio(servicio.getTipoServicio() != null ? servicio.getTipoServicio().getIdTipoServicio() : null);
        dto.setFecha(servicio.getFecha());
        dto.setHora(servicio.getHora());
        dto.setEstado(servicio.getEstado().toString()); // Convertir Enum a String para el DTO
        dto.setObservaciones(servicio.getObservaciones());
        dto.setPrioridad(servicio.getPrioridad());
        dto.setDuracionEstimada(servicio.getDuracionEstimada());
        dto.setServicioSinCotizacion(servicio.getServicioSinCotizacion());
        return dto;
    }
}