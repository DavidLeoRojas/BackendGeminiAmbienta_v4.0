package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.Cotizacion;
import com.gemini.gemini_ambiental.entity.DetalleCotizacion;
import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.entity.Producto;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.CotizacionRepository;
import com.gemini.gemini_ambiental.repository.DetalleCotizacionRepository;
import com.gemini.gemini_ambiental.repository.PersonaRepository;
import com.gemini.gemini_ambiental.repository.ProductoRepository;
import com.gemini.gemini_ambiental.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CotizacionService {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;

    // ========== MÉTODOS PARA COTIZACIONES CON PRODUCTOS ==========

    public CotizacionDTO createCotizacionFromRequest(CotizacionRequestDTO requestDTO) {
        Cotizacion cotizacion = convertRequestToEntity(requestDTO);
        Cotizacion savedCotizacion = cotizacionRepository.save(cotizacion);
        return convertToDTO(savedCotizacion);
    }

    public CotizacionDTO updateCotizacionWithProducts(String id, CotizacionRequestDTO requestDTO) {
        Cotizacion existingCotizacion = cotizacionRepository.findByIdWithAllRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        // Actualizar campos básicos
        updateCotizacionFromRequest(existingCotizacion, requestDTO);

        // Actualizar detalles si se proporcionan
        if (requestDTO.getDetalleCotizacion() != null) {
            updateDetallesCotizacion(existingCotizacion, requestDTO.getDetalleCotizacion());
        } else if (requestDTO.getCostoTotalCotizacion() != null) {
            existingCotizacion.setCostoTotalCotizacion(requestDTO.getCostoTotalCotizacion());
        }

        Cotizacion updatedCotizacion = cotizacionRepository.save(existingCotizacion);
        return convertToDTO(updatedCotizacion);
    }

    // ========== MÉTODOS EXISTENTES (compatibilidad) ==========

    public CotizacionDTO createCotizacion(CotizacionDTO cotizacionDTO) {
        Cotizacion cotizacion = convertToEntity(cotizacionDTO);
        Cotizacion savedCotizacion = cotizacionRepository.save(cotizacion);
        return convertToDTO(savedCotizacion);
    }

    public CotizacionDTO updateCotizacion(String id, CotizacionDTO cotizacionDTO) {
        Cotizacion existingCotizacion = cotizacionRepository.findByIdWithAllRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        existingCotizacion.setEstado(cotizacionDTO.getEstado());
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

    // ========== MÉTODOS DE CONSULTA ==========

    public CotizacionDTO getCotizacionById(String id) {
        Cotizacion cotizacion = cotizacionRepository.findByIdWithAllRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));
        return convertToDTO(cotizacion);
    }

    public CotizacionDTO getCotizacionByIdWithDetails(String id) {
        Cotizacion cotizacion = cotizacionRepository.findByIdWithAllRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));
        return convertToDTO(cotizacion);
    }

    public List<CotizacionDTO> getAllCotizaciones() {
        List<Cotizacion> cotizaciones = cotizacionRepository.findAllBasic();
        return cotizaciones.stream()
                .map(this::convertToDTOForList)
                .collect(Collectors.toList());
    }

    public List<CotizacionDTO> getCotizacionesByCliente(String dniCliente) {
        List<Cotizacion> cotizaciones = cotizacionRepository.findByClienteDni(dniCliente);
        return cotizaciones.stream()
                .map(this::convertToDTOForList)
                .collect(Collectors.toList());
    }

    public List<CotizacionDTO> searchCotizaciones(String fechaInicio, String fechaFin, String estado, String dniCliente, String dniEmpleado) {
        LocalDateTime start = fechaInicio != null ? LocalDateTime.parse(fechaInicio) : null;
        LocalDateTime end = fechaFin != null ? LocalDateTime.parse(fechaFin) : null;

        List<Cotizacion> cotizaciones = cotizacionRepository.findAllBasic();

        // Aplicar filtros
        if (start != null && end != null) {
            cotizaciones = cotizaciones.stream()
                    .filter(c -> !c.getFechaSolicitud().isBefore(start) && !c.getFechaSolicitud().isAfter(end))
                    .collect(Collectors.toList());
        }

        if (estado != null && !estado.isEmpty()) {
            cotizaciones = cotizaciones.stream()
                    .filter(c -> c.getEstado() != null && c.getEstado().equalsIgnoreCase(estado))
                    .collect(Collectors.toList());
        }

        if (dniCliente != null && !dniCliente.isEmpty()) {
            cotizaciones = cotizaciones.stream()
                    .filter(c -> c.getCliente() != null && c.getCliente().getDni().equals(dniCliente))
                    .collect(Collectors.toList());
        }

        if (dniEmpleado != null && !dniEmpleado.isEmpty()) {
            cotizaciones = cotizaciones.stream()
                    .filter(c -> c.getEmpleado() != null && c.getEmpleado().getDni().equals(dniEmpleado))
                    .collect(Collectors.toList());
        }

        return cotizaciones.stream()
                .map(this::convertToDTOForList)
                .collect(Collectors.toList());
    }

    // ========== MÉTODOS ADICIONALES ==========

    public CotizacionDTO cambiarEstadoCotizacion(String id, String nuevoEstado) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        cotizacion.setEstado(nuevoEstado);

        // Si se aprueba o finaliza, establecer fecha de respuesta
        if ("APROBADA".equals(nuevoEstado) || "FINALIZADA".equals(nuevoEstado)) {
            cotizacion.setFechaRespuesta(LocalDate.now());
        }

        Cotizacion updatedCotizacion = cotizacionRepository.save(cotizacion);
        return convertToDTO(updatedCotizacion);
    }

    public Long contarCotizacionesPorEstado(String estado) {
        return cotizacionRepository.countByEstado(estado);
    }

    // ========== MÉTODOS PRIVADOS DE CONVERSIÓN ==========

    private Cotizacion convertRequestToEntity(CotizacionRequestDTO requestDTO) {
        Cotizacion cotizacion = new Cotizacion();

        // Configurar cliente
        if (requestDTO.getDniCliente() != null) {
            Persona cliente = personaRepository.findByDni(requestDTO.getDniCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con DNI: " + requestDTO.getDniCliente()));
            cotizacion.setCliente(cliente);
        }

        // Configurar campos básicos
        cotizacion.setFechaSolicitud(LocalDateTime.now());
        cotizacion.setFechaPreferida(requestDTO.getFechaPreferida());
        cotizacion.setPrioridad(requestDTO.getPrioridad() != null ? requestDTO.getPrioridad() : "Media");
        cotizacion.setEstado(requestDTO.getEstado() != null ? requestDTO.getEstado() : "PENDIENTE");
        cotizacion.setDescripcionProblema(requestDTO.getDescripcionProblema());
        cotizacion.setNotasInternas(requestDTO.getNotasInternas());

        // ✅ CORRECCIÓN: Inicializar la lista de detalles
        cotizacion.setDetalleCotizacion(new ArrayList<>());

        // Configurar detalles de cotización
        if (requestDTO.getDetalleCotizacion() != null && !requestDTO.getDetalleCotizacion().isEmpty()) {
            for (DetalleCotizacionRequestDTO detalleRequest : requestDTO.getDetalleCotizacion()) {
                Producto producto = productoRepository.findById(detalleRequest.getIdProducto())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleRequest.getIdProducto()));

                DetalleCotizacion detalle = new DetalleCotizacion();
                detalle.setCotizacion(cotizacion);
                detalle.setProducto(producto);
                detalle.setCantidad(detalleRequest.getCantidad());
                detalle.setPrecioUnitario(detalleRequest.getPrecioUnitario() != null ?
                        detalleRequest.getPrecioUnitario() : BigDecimal.ZERO);

                cotizacion.getDetalleCotizacion().add(detalle);
            }

            // Calcular costo total automáticamente desde los detalles
            BigDecimal costoTotal = cotizacion.getDetalleCotizacion().stream()
                    .map(detalle -> detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cotizacion.setCostoTotalCotizacion(costoTotal);
        } else {
            // Si no hay detalles, usar el costo total proporcionado
            cotizacion.setCostoTotalCotizacion(requestDTO.getCostoTotalCotizacion() != null ?
                    requestDTO.getCostoTotalCotizacion() : BigDecimal.ZERO);
        }

        return cotizacion;
    }

    private void updateCotizacionFromRequest(Cotizacion cotizacion, CotizacionRequestDTO requestDTO) {
        if (requestDTO.getEstado() != null) {
            cotizacion.setEstado(requestDTO.getEstado());
        }
        if (requestDTO.getFechaPreferida() != null) {
            cotizacion.setFechaPreferida(requestDTO.getFechaPreferida());
        }
        if (requestDTO.getPrioridad() != null) {
            cotizacion.setPrioridad(requestDTO.getPrioridad());
        }
        if (requestDTO.getDescripcionProblema() != null) {
            cotizacion.setDescripcionProblema(requestDTO.getDescripcionProblema());
        }
        if (requestDTO.getNotasInternas() != null) {
            cotizacion.setNotasInternas(requestDTO.getNotasInternas());
        }
    }

    private void updateDetallesCotizacion(Cotizacion cotizacion, List<DetalleCotizacionRequestDTO> detallesRequest) {
        // Limpiar detalles existentes
        cotizacion.getDetalleCotizacion().clear();

        // Agregar nuevos detalles
        for (DetalleCotizacionRequestDTO detalleRequest : detallesRequest) {
            Producto producto = productoRepository.findById(detalleRequest.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleRequest.getIdProducto()));

            DetalleCotizacion detalle = new DetalleCotizacion();
            detalle.setCotizacion(cotizacion);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setPrecioUnitario(detalleRequest.getPrecioUnitario() != null ?
                    detalleRequest.getPrecioUnitario() : BigDecimal.ZERO);

            cotizacion.getDetalleCotizacion().add(detalle);
        }

        // Recalcular costo total
        BigDecimal costoTotal = cotizacion.getDetalleCotizacion().stream()
                .map(detalle -> detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cotizacion.setCostoTotalCotizacion(costoTotal);
    }

    private Cotizacion convertToEntity(CotizacionDTO dto) {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setIdCotizacion(dto.getIdCotizacion());
        cotizacion.setEstado(dto.getEstado());
        cotizacion.setFechaSolicitud(dto.getFechaSolicitud());
        cotizacion.setFechaPreferida(dto.getFechaPreferida());
        cotizacion.setFechaRespuesta(dto.getFechaRespuesta());
        cotizacion.setPrioridad(dto.getPrioridad());
        cotizacion.setDescripcionProblema(dto.getDescripcionProblema());
        cotizacion.setNotasInternas(dto.getNotasInternas());
        cotizacion.setCostoTotalCotizacion(dto.getCostoTotalCotizacion());

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
        dto.setEstado(cotizacion.getEstado());
        dto.setFechaSolicitud(cotizacion.getFechaSolicitud());
        dto.setFechaPreferida(cotizacion.getFechaPreferida());
        dto.setFechaRespuesta(cotizacion.getFechaRespuesta());
        dto.setPrioridad(cotizacion.getPrioridad());
        dto.setDescripcionProblema(cotizacion.getDescripcionProblema());
        dto.setNotasInternas(cotizacion.getNotasInternas());
        dto.setCostoTotalCotizacion(cotizacion.getCostoTotalCotizacion());
        dto.setFechaCreacion(cotizacion.getFechaCreacion());

        // Información del cliente
        if (cotizacion.getCliente() != null) {
            dto.setNombreCliente(cotizacion.getCliente().getNombre());
            dto.setTelefonoCliente(cotizacion.getCliente().getTelefono());
            dto.setCorreoCliente(cotizacion.getCliente().getCorreo());
        }

        // Información del empleado
        if (cotizacion.getEmpleado() != null) {
            dto.setNombreEmpleado(cotizacion.getEmpleado().getNombre());
            dto.setTelefonoEmpleado(cotizacion.getEmpleado().getTelefono());
            dto.setCorreoEmpleado(cotizacion.getEmpleado().getCorreo());
        }

        // Detalles de cotización
        if (cotizacion.getDetalleCotizacion() != null && !cotizacion.getDetalleCotizacion().isEmpty()) {
            List<DetalleCotizacionDTO> detallesDto = cotizacion.getDetalleCotizacion().stream()
                    .map(det -> {
                        DetalleCotizacionDTO detDto = new DetalleCotizacionDTO();
                        detDto.setId(det.getId());
                        detDto.setIdCotizacion(det.getCotizacion().getIdCotizacion());
                        detDto.setIdProducto(det.getProducto().getIdProducto());
                        detDto.setNombreProducto(det.getProducto().getNombre());
                        detDto.setCantidad(det.getCantidad());
                        detDto.setPrecioUnitario(det.getPrecioUnitario());
                        detDto.setSubtotal(det.getSubtotal());
                        return detDto;
                    })
                    .collect(Collectors.toList());
            dto.setDetalleCotizacion(detallesDto);
        }

        return dto;
    }

    private CotizacionDTO convertToDTOForList(Cotizacion cotizacion) {
        CotizacionDTO dto = new CotizacionDTO();
        dto.setIdCotizacion(cotizacion.getIdCotizacion());
        dto.setDniCliente(cotizacion.getCliente() != null ? cotizacion.getCliente().getDni() : null);
        dto.setDniEmpleado(cotizacion.getEmpleado() != null ? cotizacion.getEmpleado().getDni() : null);
        dto.setEstado(cotizacion.getEstado());
        dto.setFechaSolicitud(cotizacion.getFechaSolicitud());
        dto.setFechaPreferida(cotizacion.getFechaPreferida());
        dto.setFechaRespuesta(cotizacion.getFechaRespuesta());
        dto.setPrioridad(cotizacion.getPrioridad());
        dto.setDescripcionProblema(cotizacion.getDescripcionProblema());
        dto.setNotasInternas(cotizacion.getNotasInternas());
        dto.setCostoTotalCotizacion(cotizacion.getCostoTotalCotizacion());
        dto.setFechaCreacion(cotizacion.getFechaCreacion());

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

        // ✅ CORRECCIÓN: Incluir información básica de productos para la lista
        if (cotizacion.getDetalleCotizacion() != null && !cotizacion.getDetalleCotizacion().isEmpty()) {
            List<DetalleCotizacionDTO> detallesBasicos = cotizacion.getDetalleCotizacion().stream()
                    .map(det -> {
                        DetalleCotizacionDTO detDto = new DetalleCotizacionDTO();
                        detDto.setId(det.getId());
                        detDto.setIdCotizacion(det.getCotizacion().getIdCotizacion());
                        detDto.setIdProducto(det.getProducto().getIdProducto());
                        detDto.setNombreProducto(det.getProducto().getNombre());
                        detDto.setCantidad(det.getCantidad());
                        detDto.setPrecioUnitario(det.getPrecioUnitario());
                        detDto.setSubtotal(det.getSubtotal());
                        return detDto;
                    })
                    .collect(Collectors.toList());
            dto.setDetalleCotizacion(detallesBasicos);
        } else {
            dto.setDetalleCotizacion(new ArrayList<>());
        }

        return dto;
    }
}