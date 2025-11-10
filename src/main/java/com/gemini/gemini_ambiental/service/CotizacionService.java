package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.*;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.*;
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
    private TipoServicioRepository tipoServicioRepository;

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
            // ✅ CORRECCIÓN: ASEGURARSE DE ASIGNAR EL COSTO TOTAL ENVIADO DESPUÉS DE ACTUALIZAR DETALLES
            // Siempre usar el valor enviado por el frontend, incluso si se actualizan los detalles
            if (requestDTO.getCostoTotalCotizacion() != null) {
                existingCotizacion.setCostoTotalCotizacion(requestDTO.getCostoTotalCotizacion());
            }
            // El valorServicio ya se actualizó en updateCotizacionFromRequest si estaba en el DTO
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

        if (cotizacionDTO.getEstado() != null) {
            existingCotizacion.setEstado(Cotizacion.EstadoCotizacion.valueOf(cotizacionDTO.getEstado()));
        }
        existingCotizacion.setFechaPreferida(cotizacionDTO.getFechaPreferida());
        existingCotizacion.setFechaRespuesta(cotizacionDTO.getFechaRespuesta());
        existingCotizacion.setPrioridad(cotizacionDTO.getPrioridad());
        existingCotizacion.setDescripcionProblema(cotizacionDTO.getDescripcionProblema());
        existingCotizacion.setNotasInternas(cotizacionDTO.getNotasInternas());
        existingCotizacion.setCostoTotalCotizacion(cotizacionDTO.getCostoTotalCotizacion());
        // ✅ Actualizar el Valor del Servicio si se proporciona en el DTO antiguo
        if (cotizacionDTO.getValorServicio() != null) {
            existingCotizacion.setValorServicio(cotizacionDTO.getValorServicio());
        }

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
                    .filter(c -> c.getEstado() != null && c.getEstado().name().equalsIgnoreCase(estado))
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

        cotizacion.setEstado(Cotizacion.EstadoCotizacion.valueOf(nuevoEstado));

        // Si se aprueba o finaliza, establecer fecha de respuesta
        if ("APROBADA".equals(nuevoEstado) || "FINALIZADA".equals(nuevoEstado)) {
            cotizacion.setFechaRespuesta(LocalDate.now());
        }

        Cotizacion updatedCotizacion = cotizacionRepository.save(cotizacion);
        return convertToDTO(updatedCotizacion);
    }

    public Long contarCotizacionesPorEstado(String estado) {
        return cotizacionRepository.countByEstado(Cotizacion.EstadoCotizacion.valueOf(estado));
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

        if (requestDTO.getEstado() != null) {
            cotizacion.setEstado(Cotizacion.EstadoCotizacion.valueOf(requestDTO.getEstado()));
        } else {
            cotizacion.setEstado(Cotizacion.EstadoCotizacion.PENDIENTE);
        }

        cotizacion.setDescripcionProblema(requestDTO.getDescripcionProblema());
        cotizacion.setNotasInternas(requestDTO.getNotasInternas());
        cotizacion.setValorServicio(requestDTO.getValorServicio() != null ? requestDTO.getValorServicio() : BigDecimal.ZERO);
        cotizacion.setDetalleCotizacion(new ArrayList<>());

        // Configurar detalles de cotización
        if (requestDTO.getDetalleCotizacion() != null && !requestDTO.getDetalleCotizacion().isEmpty()) {
            for (DetalleCotizacionRequestDTO detalleRequest : requestDTO.getDetalleCotizacion()) {
                Producto producto = productoRepository.findById(detalleRequest.getIdProducto())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleRequest.getIdProducto()));

                // 🔥 OBTENER TIPO DE SERVICIO
                TipoServicio tipoServicio = tipoServicioRepository.findById(
                        detalleRequest.getIdTipoServicio() != null ?
                                detalleRequest.getIdTipoServicio() : "SERV_DEFAULT"
                ).orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de servicio no encontrado con ID: " + detalleRequest.getIdTipoServicio()));

                DetalleCotizacion detalle = DetalleCotizacion.builder()
                        .cotizacion(cotizacion)
                        .producto(producto)
                        .tipoServicio(tipoServicio) // 🔥 ASIGNAR TIPO SERVICIO
                        .cantidad(detalleRequest.getCantidad())
                        .precioUnitario(detalleRequest.getPrecioUnitario() != null ?
                                detalleRequest.getPrecioUnitario() : BigDecimal.ZERO)
                        .subtotal(detalleRequest.getSubtotal() != null ?
                                detalleRequest.getSubtotal() : BigDecimal.ZERO)
                        .build();

                cotizacion.getDetalleCotizacion().add(detalle);
            }
        }

        cotizacion.setCostoTotalCotizacion(requestDTO.getCostoTotalCotizacion() != null ?
                requestDTO.getCostoTotalCotizacion() : BigDecimal.ZERO);

        return cotizacion;
    }

    private void updateCotizacionFromRequest(Cotizacion cotizacion, CotizacionRequestDTO requestDTO) {
        if (requestDTO.getEstado() != null) {
            cotizacion.setEstado(Cotizacion.EstadoCotizacion.valueOf(requestDTO.getEstado()));
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
        // ✅ Actualizar el Valor del Servicio si se proporciona
        if (requestDTO.getValorServicio() != null) {
            cotizacion.setValorServicio(requestDTO.getValorServicio());
        }
    }

    private void updateDetallesCotizacion(Cotizacion cotizacion, List<DetalleCotizacionRequestDTO> detallesRequest) {
        // Limpiar detalles existentes
        cotizacion.getDetalleCotizacion().clear();

        // Agregar nuevos detalles
        for (DetalleCotizacionRequestDTO detalleRequest : detallesRequest) {
            Producto producto = productoRepository.findById(detalleRequest.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleRequest.getIdProducto()));

            // 🔥 OBTENER TIPO DE SERVICIO
            TipoServicio tipoServicio = tipoServicioRepository.findById(
                    detalleRequest.getIdTipoServicio() != null ?
                            detalleRequest.getIdTipoServicio() : "SERV_DEFAULT"
            ).orElseThrow(() -> new ResourceNotFoundException(
                    "Tipo de servicio no encontrado con ID: " + detalleRequest.getIdTipoServicio()));

            DetalleCotizacion detalle = DetalleCotizacion.builder()
                    .cotizacion(cotizacion)
                    .producto(producto)
                    .tipoServicio(tipoServicio) // 🔥 ASIGNAR TIPO SERVICIO
                    .cantidad(detalleRequest.getCantidad())
                    .precioUnitario(detalleRequest.getPrecioUnitario() != null ?
                            detalleRequest.getPrecioUnitario() : BigDecimal.ZERO)
                    .subtotal(detalleRequest.getSubtotal() != null ?
                            detalleRequest.getSubtotal() : BigDecimal.ZERO)
                    .build();

            cotizacion.getDetalleCotizacion().add(detalle);
        }
    }

    private Cotizacion convertToEntity(CotizacionDTO dto) {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setIdCotizacion(dto.getIdCotizacion());

        // ✅ CORREGIDO: Convertir String a Enum
        if (dto.getEstado() != null) {
            cotizacion.setEstado(Cotizacion.EstadoCotizacion.valueOf(dto.getEstado()));
        }

        cotizacion.setFechaSolicitud(dto.getFechaSolicitud());
        cotizacion.setFechaPreferida(dto.getFechaPreferida());
        cotizacion.setFechaRespuesta(dto.getFechaRespuesta());
        cotizacion.setPrioridad(dto.getPrioridad());
        cotizacion.setDescripcionProblema(dto.getDescripcionProblema());
        cotizacion.setNotasInternas(dto.getNotasInternas());
        cotizacion.setCostoTotalCotizacion(dto.getCostoTotalCotizacion());
        // ✅ Asignar el Valor del Servicio si está en el DTO
        if (dto.getValorServicio() != null) {
            cotizacion.setValorServicio(dto.getValorServicio());
        }

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

        // ✅ CORREGIDO: Convertir Enum a String
        dto.setEstado(cotizacion.getEstado() != null ? cotizacion.getEstado().name() : null);

        dto.setFechaSolicitud(cotizacion.getFechaSolicitud());
        dto.setFechaPreferida(cotizacion.getFechaPreferida());
        dto.setFechaRespuesta(cotizacion.getFechaRespuesta());
        dto.setPrioridad(cotizacion.getPrioridad());
        dto.setDescripcionProblema(cotizacion.getDescripcionProblema());
        dto.setNotasInternas(cotizacion.getNotasInternas());
        dto.setCostoTotalCotizacion(cotizacion.getCostoTotalCotizacion());
        // ✅ Mapear el Valor del Servicio
        dto.setValorServicio(cotizacion.getValorServicio());
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
// Detalles de cotización
        if (cotizacion.getDetalleCotizacion() != null && !cotizacion.getDetalleCotizacion().isEmpty()) {
            List<DetalleCotizacionDTO> detallesDto = cotizacion.getDetalleCotizacion().stream()
                    .map(this::convertDetalleToDTO)  // ← USA EL MÉTODO NUEVO
                    .collect(Collectors.toList());
            dto.setDetalleCotizacion(detallesDto);
        }

        return dto;
    }


    private DetalleCotizacionDTO convertDetalleToDTO(DetalleCotizacion detalle) {
        DetalleCotizacionDTO detalleDTO = new DetalleCotizacionDTO();
        detalleDTO.setId(detalle.getId());
        detalleDTO.setIdCotizacion(detalle.getCotizacion().getIdCotizacion());
        detalleDTO.setCantidad(detalle.getCantidad());
        detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario());
        detalleDTO.setSubtotal(detalle.getSubtotal());

        // Información del producto
        if (detalle.getProducto() != null) {
            detalleDTO.setIdProducto(detalle.getProducto().getIdProducto());
            detalleDTO.setNombreProducto(detalle.getProducto().getNombre());
        } else {
            detalleDTO.setIdProducto("PRODUCTO_ELIMINADO");
            detalleDTO.setNombreProducto("Producto no disponible");
        }

        // 🔥 INFORMACIÓN DEL TIPO DE SERVICIO
        if (detalle.getTipoServicio() != null) {
            detalleDTO.setIdTipoServicio(detalle.getTipoServicio().getIdTipoServicio());
            detalleDTO.setNombreTipoServicio(detalle.getTipoServicio().getNombreServicio());
        } else {
            detalleDTO.setIdTipoServicio("SERV_DEFAULT");
            detalleDTO.setNombreTipoServicio("Servicio General");
        }

        return detalleDTO;
    }



    private CotizacionDTO convertToDTOForList(Cotizacion cotizacion) {
        CotizacionDTO dto = new CotizacionDTO();
        dto.setIdCotizacion(cotizacion.getIdCotizacion());
        dto.setDniCliente(cotizacion.getCliente() != null ? cotizacion.getCliente().getDni() : null);
        dto.setDniEmpleado(cotizacion.getEmpleado() != null ? cotizacion.getEmpleado().getDni() : null);

        // ✅ CORREGIDO: Convertir Enum a String
        dto.setEstado(cotizacion.getEstado() != null ? cotizacion.getEstado().name() : null);

        dto.setFechaSolicitud(cotizacion.getFechaSolicitud());
        dto.setFechaPreferida(cotizacion.getFechaPreferida());
        dto.setFechaRespuesta(cotizacion.getFechaRespuesta());
        dto.setPrioridad(cotizacion.getPrioridad());
        dto.setDescripcionProblema(cotizacion.getDescripcionProblema());
        dto.setNotasInternas(cotizacion.getNotasInternas());
        dto.setCostoTotalCotizacion(cotizacion.getCostoTotalCotizacion());
        // ✅ Mapear el Valor del Servicio
        dto.setValorServicio(cotizacion.getValorServicio());
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
// ✅ CORRECCIÓN: Incluir información básica de productos para la lista
        if (cotizacion.getDetalleCotizacion() != null && !cotizacion.getDetalleCotizacion().isEmpty()) {
            List<DetalleCotizacionDTO> detallesBasicos = cotizacion.getDetalleCotizacion().stream()
                    .map(this::convertDetalleToDTO)  // ← USA EL MÉTODO NUEVO
                    .collect(Collectors.toList());
            dto.setDetalleCotizacion(detallesBasicos);
        } else {
            dto.setDetalleCotizacion(new ArrayList<>());
        }

        return dto;
    }

}