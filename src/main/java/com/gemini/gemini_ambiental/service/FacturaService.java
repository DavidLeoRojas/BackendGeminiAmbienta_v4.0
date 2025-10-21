package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.*;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.*;
import com.gemini.gemini_ambiental.dto.FacturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public FacturaDTO createFactura(FacturaDTO facturaDTO) {
        Factura factura = convertToEntity(facturaDTO);
        Factura savedFactura = facturaRepository.save(factura);
        actualizarStocks(savedFactura);
        return convertToDTO(savedFactura);
    }

    public FacturaDTO updateFactura(String id, FacturaDTO facturaDTO) {
        Factura existingFactura = facturaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));

        // Actualizar cliente
        if (facturaDTO.getDniCliente() != null) {
            Persona cliente = personaRepository.findByDni(facturaDTO.getDniCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con DNI: " + facturaDTO.getDniCliente()));
            existingFactura.setCliente(cliente);
        }

        // Actualizar cotización si aplica
        if ("ConCotizacion".equals(facturaDTO.getTipoFactura()) && facturaDTO.getIdCotizacion() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(facturaDTO.getIdCotizacion())
                    .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + facturaDTO.getIdCotizacion()));
            existingFactura.setCotizacion(cotizacion);
        } else {
            existingFactura.setCotizacion(null);
        }

        // Actualizar otros campos
        existingFactura.setFechaEmision(facturaDTO.getFechaEmision());
        existingFactura.setMontoTotal(facturaDTO.getMontoTotal());
        existingFactura.setEstado(Factura.EstadoFactura.valueOf(facturaDTO.getEstado()));
        existingFactura.setObservaciones(facturaDTO.getObservaciones());
        existingFactura.setTipoFactura(Factura.TipoFactura.valueOf(facturaDTO.getTipoFactura()));

        // --- Manejar DetalleFactura ---
        existingFactura.getDetalleFactura().clear();

        if (facturaDTO.getDetalleFactura() != null && !facturaDTO.getDetalleFactura().isEmpty()) {
            for (FacturaDTO.DetalleFacturaDTO dtoDetalle : facturaDTO.getDetalleFactura()) {
                Producto producto = productoRepository.findById(dtoDetalle.getIdProducto())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dtoDetalle.getIdProducto()));

                // ✅ Calcular precios desde el producto
                BigDecimal precioUnitario = producto.getPrecioActual();
                Integer cantidad = dtoDetalle.getCantidad();
                BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

                DetalleFactura detalle = DetalleFactura.builder()
                        .producto(producto)
                        .cantidad(cantidad)
                        .precioUnitario(precioUnitario)
                        .subtotal(subtotal)
                        .build();

                existingFactura.addDetalleFactura(detalle);
            }
        }

        Factura updatedFactura = facturaRepository.save(existingFactura);
        actualizarStocks(updatedFactura);
        return convertToDTO(updatedFactura);
    }

    public void deleteFactura(String id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));
        facturaRepository.delete(factura);
    }

    public FacturaDTO getFacturaById(String id) {
        Factura factura = facturaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));
        return convertToDTO(factura);
    }

    public List<FacturaDTO> getAllFacturas() {
        return facturaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FacturaDTO> searchFacturas(String fechaInicio, String fechaFin, String estado, String tipoFactura, String dniCliente) {
        LocalDate start = fechaInicio != null ? LocalDate.parse(fechaInicio) : null;
        LocalDate end = fechaFin != null ? LocalDate.parse(fechaFin) : null;

        List<Factura> facturas = facturaRepository.findAll();

        if (start != null && end != null) {
            facturas = facturas.stream()
                    .filter(f -> !f.getFechaEmision().isBefore(start) && !f.getFechaEmision().isAfter(end))
                    .collect(Collectors.toList());
        }

        if (estado != null) {
            Factura.EstadoFactura estadoEnum = Factura.EstadoFactura.valueOf(estado);
            facturas = facturas.stream()
                    .filter(f -> f.getEstado() == estadoEnum)
                    .collect(Collectors.toList());
        }

        if (tipoFactura != null) {
            Factura.TipoFactura tipoEnum = Factura.TipoFactura.valueOf(tipoFactura);
            facturas = facturas.stream()
                    .filter(f -> f.getTipoFactura() == tipoEnum)
                    .collect(Collectors.toList());
        }

        if (dniCliente != null) {
            facturas = facturas.stream()
                    .filter(f -> f.getCliente() != null && f.getCliente().getDni().equals(dniCliente))
                    .collect(Collectors.toList());
        }

        return facturas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Factura convertToEntity(FacturaDTO dto) {
        Factura factura = new Factura();

        Persona cliente = personaRepository.findByDni(dto.getDniCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con DNI: " + dto.getDniCliente()));
        factura.setCliente(cliente);

        // --- CARGAR COTIZACIÓN ---
        if ("ConCotizacion".equals(dto.getTipoFactura()) && dto.getIdCotizacion() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(dto.getIdCotizacion())
                    .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + dto.getIdCotizacion()));
            factura.setCotizacion(cotizacion);

            // --- CARGAR PRODUCTOS DE LA COTIZACIÓN ---
            if (cotizacion.getDetalleCotizacion() != null && !cotizacion.getDetalleCotizacion().isEmpty()) {
                for (com.gemini.gemini_ambiental.entity.DetalleCotizacion detCot : cotizacion.getDetalleCotizacion()) {
                    Producto producto = productoRepository.findById(detCot.getProducto().getIdProducto())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detCot.getProducto().getIdProducto()));

                    // Calcular precios
                    BigDecimal precioUnitario = producto.getPrecioActual();
                    Integer cantidad = detCot.getCantidad();
                    BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

                    DetalleFactura detalle = DetalleFactura.builder()
                            .producto(producto)
                            .cantidad(cantidad)
                            .precioUnitario(precioUnitario)
                            .subtotal(subtotal)
                            .build();

                    factura.addDetalleFactura(detalle);
                }
            }
        }

        // --- AGREGAR PRODUCTOS DESDE EL FORMULARIO (SOBREESCRIBE LOS DE LA COTIZACIÓN SI ES NECESARIO) ---
        if (dto.getDetalleFactura() != null && !dto.getDetalleFactura().isEmpty()) {
            // Limpiar los detalles cargados de la cotización si se envían desde el formulario
            factura.getDetalleFactura().clear();

            for (FacturaDTO.DetalleFacturaDTO dtoDetalle : dto.getDetalleFactura()) {
                Producto producto = productoRepository.findById(dtoDetalle.getIdProducto())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dtoDetalle.getIdProducto()));

                BigDecimal precioUnitario = producto.getPrecioActual();
                Integer cantidad = dtoDetalle.getCantidad();
                BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

                DetalleFactura detalle = DetalleFactura.builder()
                        .producto(producto)
                        .cantidad(cantidad)
                        .precioUnitario(precioUnitario)
                        .subtotal(subtotal)
                        .build();

                factura.addDetalleFactura(detalle);
            }
        }

        factura.setFechaEmision(dto.getFechaEmision());
        factura.setMontoTotal(dto.getMontoTotal());
        factura.setEstado(Factura.EstadoFactura.valueOf(dto.getEstado()));
        factura.setObservaciones(dto.getObservaciones());
        factura.setTipoFactura(Factura.TipoFactura.valueOf(dto.getTipoFactura()));
        factura.setValorServicio(dto.getValorServicio());

        return factura;
    }

    private void actualizarStocks(Factura factura) {
        if (factura.getDetalleFactura() != null && !factura.getDetalleFactura().isEmpty()) {
            // Este código se ejecutará siempre que haya productos en el detalle, sin importar el tipo de factura
            for (DetalleFactura detalle : factura.getDetalleFactura()) {
                String idProducto = detalle.getProducto().getIdProducto();
                Integer cantidadUsada = detalle.getCantidad();

                Optional<Producto> optionalProducto = productoRepository.findById(idProducto);
                if (optionalProducto.isEmpty()) {
                    throw new RuntimeException("Producto no encontrado: " + idProducto);
                }
                Producto producto = optionalProducto.get();
                if (producto.getStock() < cantidadUsada) {
                    throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
                }

                // Actualizar stock directamente en la BD
                int filasActualizadas = productoRepository.restarStock(idProducto, cantidadUsada);
                if (filasActualizadas == 0) {
                    throw new RuntimeException("No se pudo actualizar el stock del producto: " + idProducto + " (stock insuficiente)");
                }
            }
        }
    }

    private FacturaDTO convertToDTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setValorServicio(factura.getValorServicio());
        dto.setIdFactura(factura.getIdFactura());
        dto.setDniCliente(factura.getCliente() != null ? factura.getCliente().getDni() : null);
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setMontoTotal(factura.getMontoTotal());
        dto.setEstado(factura.getEstado().toString());
        dto.setObservaciones(factura.getObservaciones());
        dto.setTipoFactura(factura.getTipoFactura().toString());
        dto.setIdCotizacion(factura.getCotizacion() != null ? factura.getCotizacion().getIdCotizacion() : null);
        dto.setFechaCreacion(factura.getFechaCreacion());

        if (factura.getCliente() != null) {
            dto.setNombreCliente(factura.getCliente().getNombre());
            dto.setTelefonoCliente(factura.getCliente().getTelefono());
            dto.setCorreoCliente(factura.getCliente().getCorreo());
        }

        // --- Convertir DetalleFactura ---
        if (factura.getDetalleFactura() != null) {
            dto.setDetalleFactura(factura.getDetalleFactura().stream()
                    .map(det -> {
                        FacturaDTO.DetalleFacturaDTO detalleDto = new FacturaDTO.DetalleFacturaDTO();
                        detalleDto.setIdDetalleFactura(det.getIdDetalleFactura());
                        detalleDto.setIdProducto(det.getProducto().getIdProducto());
                        detalleDto.setCantidad(det.getCantidad());
                        detalleDto.setSubtotal(det.getSubtotal());
                        detalleDto.setPrecioUnitario(det.getPrecioUnitario());
                        return detalleDto;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}