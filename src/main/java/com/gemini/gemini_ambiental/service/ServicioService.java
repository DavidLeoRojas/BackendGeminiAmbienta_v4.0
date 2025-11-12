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

    public ServicioDTO createServicio(ServicioDTO dto) {

        // Generar ID si no viene
        if (dto.getIdServicio() == null || dto.getIdServicio().isEmpty()) {
            dto.setIdServicio(java.util.UUID.randomUUID().toString());
        }

        Servicio servicio = new Servicio();
        servicio.setIdServicio(dto.getIdServicio());
        servicio.setFecha(dto.getFecha());
        servicio.setHora(dto.getHora());
        servicio.setDuracionEstimada(dto.getDuracionEstimada());
        servicio.setObservaciones(dto.getObservaciones());
        servicio.setPrioridad(dto.getPrioridad() != null ? dto.getPrioridad() : "Normal");
        servicio.setEstado(mapStringToEstado(dto.getEstado()));
        servicio.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        servicio.setFechaCreacion(java.time.LocalDateTime.now());

        // Relación Cliente
        Persona cliente = personaRepository.findByDni(dto.getDniCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + dto.getDniCliente()));
        servicio.setCliente(cliente);

        // Relación Técnico
        Persona tecnico = personaRepository.findByDni(dto.getDniEmpleadoAsignado())
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado: " + dto.getDniEmpleadoAsignado()));
        servicio.setEmpleadoAsignado(tecnico);

        // Relación Tipo Servicio
        TipoServicio tipoServicio = tipoServicioRepository.findById(dto.getIdTipoServicio())
                .orElseThrow(() -> new RuntimeException("Tipo servicio no encontrado: " + dto.getIdTipoServicio()));
        servicio.setTipoServicio(tipoServicio);

        // Manejo de cotización
        if (Boolean.TRUE.equals(dto.getServicioSinCotizacion())) {
            servicio.setCotizacion(null);
        } else if (dto.getIdCotizacion() != null) {
            Cotizacion cot = cotizacionRepository.findById(dto.getIdCotizacion())
                    .orElseThrow(() -> new RuntimeException("Cotización no encontrada: " + dto.getIdCotizacion()));
            servicio.setCotizacion(cot);
        } else {
            servicio.setCotizacion(null);
        }

        servicioRepository.save(servicio);

        return convertToDTO(servicio);
    }


    public ServicioDTO updateServicio(String id, ServicioDTO servicioDTO) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));

        servicio.setFecha(servicioDTO.getFecha());
        servicio.setHora(servicioDTO.getHora());
        servicio.setEstado(mapStringToEstado(servicioDTO.getEstado()));
        servicio.setObservaciones(servicioDTO.getObservaciones());
        servicio.setPrioridad(servicioDTO.getPrioridad());
        servicio.setDuracionEstimada(servicioDTO.getDuracionEstimada());

        if (servicioDTO.getIdCotizacion() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(servicioDTO.getIdCotizacion())
                    .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + servicioDTO.getIdCotizacion()));
            servicio.setCotizacion(cotizacion);
        } else {
            servicio.setCotizacion(null);
        }

        if (servicioDTO.getDniCliente() != null) {
            Persona cliente = personaRepository.findByDni(servicioDTO.getDniCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con DNI: " + servicioDTO.getDniCliente()));
            servicio.setCliente(cliente);
        } else {
            servicio.setCliente(null);
        }

        if (servicioDTO.getDniEmpleadoAsignado() != null) {
            Persona tecnico = personaRepository.findByDni(servicioDTO.getDniEmpleadoAsignado())
                    .orElseThrow(() -> new RuntimeException("Técnico no encontrado con DNI: " + servicioDTO.getDniEmpleadoAsignado()));
            servicio.setEmpleadoAsignado(tecnico);
        } else {
            servicio.setEmpleadoAsignado(null);
        }

        if (servicioDTO.getIdTipoServicio() != null) {
            TipoServicio tipoServ = tipoServicioRepository.findById(servicioDTO.getIdTipoServicio())
                    .orElseThrow(() -> new RuntimeException("Tipo de Servicio no encontrado con ID: " + servicioDTO.getIdTipoServicio()));
            servicio.setTipoServicio(tipoServ);
        } else {
            servicio.setTipoServicio(null);
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

    /**
     * Convierte un String (ej: "PROGRAMADO") al enum correspondiente.
     * Es tolerante a mayúsculas/minúsculas y nulos.
     */
    private Servicio.EstadoServicio mapStringToEstado(String estadoStr) {
        if (estadoStr == null || estadoStr.trim().isEmpty()) {
            return Servicio.EstadoServicio.PROGRAMADO;
        }
        try {
            return Servicio.EstadoServicio.valueOf(estadoStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Estado de servicio desconocido: '" + estadoStr + "'. Usando PROGRAMADO por defecto.");
            return Servicio.EstadoServicio.PROGRAMADO;
        }
    }

    private ServicioDTO convertToDTO(Servicio servicio) {
        ServicioDTO dto = new ServicioDTO();

        dto.setIdServicio(servicio.getIdServicio());
        dto.setFecha(servicio.getFecha());
        dto.setHora(servicio.getHora());
        dto.setPrioridad(servicio.getPrioridad());
        dto.setDuracionEstimada(servicio.getDuracionEstimada());
        dto.setObservaciones(servicio.getObservaciones());
        dto.setEstado(servicio.getEstado().name());
        dto.setActivo(servicio.getActivo());
        dto.setFechaCreacion(servicio.getFechaCreacion());

        // Cliente
        if (servicio.getCliente() != null) {
            dto.setDniCliente(servicio.getCliente().getDni());
            dto.setNombreCliente(servicio.getCliente().getNombre());
            dto.setTelefonoCliente(servicio.getCliente().getTelefono());
            dto.setCorreoCliente(servicio.getCliente().getCorreo());
        }

        // Técnico
        if (servicio.getEmpleadoAsignado() != null) {
            dto.setDniEmpleadoAsignado(servicio.getEmpleadoAsignado().getDni());
            dto.setNombreEmpleado(servicio.getEmpleadoAsignado().getNombre());
            dto.setTelefonoEmpleado(servicio.getEmpleadoAsignado().getTelefono());
            dto.setCorreoEmpleado(servicio.getEmpleadoAsignado().getCorreo());
        }

        // Tipo servicio
        if (servicio.getTipoServicio() != null) {
            dto.setIdTipoServicio(servicio.getTipoServicio().getIdTipoServicio());
        }

        // Cotización
        if (servicio.getCotizacion() != null) {
            dto.setIdCotizacion(servicio.getCotizacion().getIdCotizacion());
            dto.setServicioSinCotizacion(false);
        } else {
            dto.setServicioSinCotizacion(true);
        }

        return dto;
    }

}