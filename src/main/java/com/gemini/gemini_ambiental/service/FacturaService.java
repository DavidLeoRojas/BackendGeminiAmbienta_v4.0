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
import java.util.ArrayList;
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
    private FacturaIdGeneratorService idGeneratorService;

    @Autowired
    private ProductoRepository productoRepository;


    // En FacturaService.java - AGREGAR ESTOS MÉTODOS
    public FacturaDTO getFacturaConProductos(String id) {
        Factura factura = facturaRepository.findByIdWithProductos(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));
        return convertToDTOWithProductos(factura);
    }

    @Transactional
    public FacturaDTO updateFacturaConProductos(String id, FacturaDTO facturaDTO) {
        try {
            System.out.println("🔄 Actualizando factura con productos: " + id);
            System.out.println("📦 Datos recibidos: " + facturaDTO.toString());

            Factura existingFactura = facturaRepository.findByIdWithProductos(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));

            // ✅ ACTUALIZAR CLIENTE
            if (facturaDTO.getDniCliente() != null) {
                Persona cliente = personaRepository.findByDni(facturaDTO.getDniCliente())
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con DNI: " + facturaDTO.getDniCliente()));
                existingFactura.setCliente(cliente);
            }

            // ✅ ACTUALIZAR PRODUCTOS - LIMPIAR Y AGREGAR NUEVOS
            existingFactura.getDetalleProductos().clear();

            if (facturaDTO.getDetalleFactura() != null && !facturaDTO.getDetalleFactura().isEmpty()) {
                for (FacturaDTO.DetalleFacturaDTO dtoDetalle : facturaDTO.getDetalleFactura()) {
                    // ✅ VALIDACIÓN ROBUSTA
                    if (dtoDetalle.getIdProducto() == null ||
                            "PRODUCTO_ELIMINADO".equals(dtoDetalle.getIdProducto()) ||
                            dtoDetalle.getCantidad() == null ||
                            dtoDetalle.getCantidad() <= 0) {
                        System.out.println("⚠️ Producto inválido omitido: " + dtoDetalle.getIdProducto());
                        continue;
                    }

                    Producto producto = productoRepository.findById(dtoDetalle.getIdProducto())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dtoDetalle.getIdProducto()));

                    // ✅ USAR PRECIO DEL DTO O DEL PRODUCTO
                    BigDecimal precioUnitario = dtoDetalle.getPrecioUnitario() != null ?
                            dtoDetalle.getPrecioUnitario() :
                            producto.getPrecioActual();

                    DetalleFacturaProducto detalle = DetalleFacturaProducto.builder()
                            .producto(producto)
                            .cantidad(dtoDetalle.getCantidad())
                            .precioUnitario(precioUnitario)
                            .build();

                    existingFactura.addDetalleProducto(detalle);
                }
            }

            // ✅ ACTUALIZAR OTROS CAMPOS CON VALIDACIONES
            existingFactura.setFechaEmision(facturaDTO.getFechaEmision());
            existingFactura.setMontoTotal(facturaDTO.getMontoTotal() != null ? facturaDTO.getMontoTotal() : BigDecimal.ZERO);

            // ✅ MANEJAR ESTADO CON VALOR POR DEFECTO
            if (facturaDTO.getEstado() != null) {
                try {
                    existingFactura.setEstado(Factura.EstadoFactura.valueOf(facturaDTO.getEstado()));
                } catch (IllegalArgumentException e) {
                    existingFactura.setEstado(Factura.EstadoFactura.PENDIENTE);
                }
            } else {
                existingFactura.setEstado(Factura.EstadoFactura.PENDIENTE);
            }

            existingFactura.setObservaciones(facturaDTO.getObservaciones());

            // ✅ MANEJAR VALOR SERVICIO (puede ser null)
            existingFactura.setValorServicio(facturaDTO.getValorServicio() != null ?
                    facturaDTO.getValorServicio() : BigDecimal.ZERO);

            // ✅ MANEJAR TIPO FACTURA
            if (facturaDTO.getTipoFactura() != null) {
                try {
                    existingFactura.setTipoFactura(Factura.TipoFactura.valueOf(facturaDTO.getTipoFactura()));
                } catch (IllegalArgumentException e) {
                    existingFactura.setTipoFactura(Factura.TipoFactura.Simple);
                }
            }

            Factura updatedFactura = facturaRepository.save(existingFactura);
            System.out.println("✅ Factura actualizada exitosamente: " + updatedFactura.getIdFactura());

            return convertToDTOWithProductos(updatedFactura);

        } catch (Exception e) {
            System.err.println("❌ Error al actualizar factura: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ✅ MÉTODO MEJORADO PARA CARGAR PRODUCTOS
    private FacturaDTO convertToDTOWithProductos(Factura factura) {
        FacturaDTO dto = convertToDTO(factura);

        // ✅ CARGAR INFORMACIÓN COMPLETA DE PRODUCTOS
        if (factura.getDetalleProductos() != null) {
            List<FacturaDTO.DetalleFacturaDTO> detalles = factura.getDetalleProductos().stream()
                    .map(det -> {
                        FacturaDTO.DetalleFacturaDTO detalleDto = new FacturaDTO.DetalleFacturaDTO();
                        detalleDto.setIdDetalleFactura(det.getIdDetalle());
                        detalleDto.setIdProducto(det.getProducto().getIdProducto());
                        detalleDto.setCantidad(det.getCantidad());
                        detalleDto.setPrecioUnitario(det.getPrecioUnitario());
                        detalleDto.setSubtotal(det.getPrecioUnitario().multiply(BigDecimal.valueOf(det.getCantidad())));

                        // ✅ INFORMACIÓN EXTRA PARA EL FRONTEND
                        detalleDto.setNombreProducto(det.getProducto().getNombre());
                        detalleDto.setStockProducto(det.getProducto().getStock());

                        return detalleDto;
                    })
                    .collect(Collectors.toList());
            dto.setDetalleFactura(detalles);
        }

        return dto;
    }
    public FacturaDTO createFactura(FacturaDTO facturaDTO) {
        Factura factura = convertToEntity(facturaDTO);
        Factura savedFactura = facturaRepository.save(factura);
        actualizarStocks(savedFactura);
        return convertToDTO(savedFactura);
    }

    public FacturaDTO updateFactura(String id, FacturaDTO facturaDTO) {
        Factura existingFactura = facturaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));

        if (facturaDTO.getDniCliente() != null) {
            Persona cliente = personaRepository.findByDni(facturaDTO.getDniCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con DNI: " + facturaDTO.getDniCliente()));
            existingFactura.setCliente(cliente);
        }

        if ("ConCotizacion".equals(facturaDTO.getTipoFactura()) && facturaDTO.getIdCotizacion() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(facturaDTO.getIdCotizacion())
                    .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + facturaDTO.getIdCotizacion()));
            existingFactura.setCotizacion(cotizacion);
        } else {
            existingFactura.setCotizacion(null);
        }

        existingFactura.setFechaEmision(facturaDTO.getFechaEmision());
        existingFactura.setMontoTotal(facturaDTO.getMontoTotal());
        existingFactura.setEstado(Factura.EstadoFactura.valueOf(facturaDTO.getEstado()));
        existingFactura.setObservaciones(facturaDTO.getObservaciones());
        existingFactura.setTipoFactura(Factura.TipoFactura.valueOf(facturaDTO.getTipoFactura()));

        // Limpiar productos anteriores
        existingFactura.getDetalleProductos().clear();

        if (facturaDTO.getDetalleFactura() != null && !facturaDTO.getDetalleFactura().isEmpty()) {
            for (FacturaDTO.DetalleFacturaDTO dtoDetalle : facturaDTO.getDetalleFactura()) {
                Producto producto = productoRepository.findById(dtoDetalle.getIdProducto())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dtoDetalle.getIdProducto()));

                BigDecimal precioUnitario = producto.getPrecioActual();
                Integer cantidad = dtoDetalle.getCantidad();
                BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

                DetalleFacturaProducto detalle = DetalleFacturaProducto.builder()
                        .producto(producto)
                        .cantidad(cantidad)
                        .precioUnitario(precioUnitario)
                        .build();

                existingFactura.addDetalleProducto(detalle);
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

    private Factura convertToEntity(FacturaDTO dto) {
        Factura factura = new Factura();

        // ✅ CORRECCIÓN: Generar ID secuencial
        String nuevoId = idGeneratorService.generarNuevoIdFactura();
        factura.setIdFactura(nuevoId);

        Persona cliente = personaRepository.findByDni(dto.getDniCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con DNI: " + dto.getDniCliente()));
        factura.setCliente(cliente);

        if ("ConCotizacion".equals(dto.getTipoFactura()) && dto.getIdCotizacion() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(dto.getIdCotizacion())
                    .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + dto.getIdCotizacion()));
            factura.setCotizacion(cotizacion);
        }

        // Agregar productos desde el formulario
        if (dto.getDetalleFactura() != null && !dto.getDetalleFactura().isEmpty()) {
            for (FacturaDTO.DetalleFacturaDTO dtoDetalle : dto.getDetalleFactura()) {
                // ✅ Validar producto antes de agregar
                if (dtoDetalle.getIdProducto() == null ||
                        "PRODUCTO_ELIMINADO".equals(dtoDetalle.getIdProducto()) ||
                        dtoDetalle.getCantidad() == null ||
                        dtoDetalle.getCantidad() <= 0) {
                    continue; // Saltar productos inválidos
                }

                Producto producto = productoRepository.findById(dtoDetalle.getIdProducto())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dtoDetalle.getIdProducto()));

                // ✅ Usar precio del DTO si viene, sino del producto
                BigDecimal precioUnitario = dtoDetalle.getPrecioUnitario() != null ?
                        dtoDetalle.getPrecioUnitario() :
                        producto.getPrecioActual();

                Integer cantidad = dtoDetalle.getCantidad();

                DetalleFacturaProducto detalle = DetalleFacturaProducto.builder()
                        .producto(producto)
                        .cantidad(cantidad)
                        .precioUnitario(precioUnitario)
                        .build();

                factura.addDetalleProducto(detalle);
            }
        }

        factura.setFechaEmision(dto.getFechaEmision());
        factura.setMontoTotal(dto.getMontoTotal());
        factura.setEstado(
                dto.getEstado() != null
                        ? Factura.EstadoFactura.valueOf(dto.getEstado())
                        : Factura.EstadoFactura.PENDIENTE
        );
        factura.setObservaciones(dto.getObservaciones());
        factura.setTipoFactura(Factura.TipoFactura.valueOf(dto.getTipoFactura()));
        factura.setValorServicio(dto.getValorServicio());

        return factura;
    }

    private void actualizarStocks(Factura factura) {
        if (factura.getDetalleProductos() != null && !factura.getDetalleProductos().isEmpty()) {
            for (DetalleFacturaProducto detalle : factura.getDetalleProductos()) {
                String idProducto = detalle.getProducto().getIdProducto();
                Integer cantidadUsada = detalle.getCantidad();

                Producto productoEnBD = productoRepository.findById(idProducto)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + idProducto));

                if (productoEnBD.getStock() < cantidadUsada) {
                    throw new RuntimeException("Stock insuficiente para el producto: " + productoEnBD.getNombre());
                }

                int filasActualizadas = productoRepository.restarStock(idProducto, cantidadUsada);
                if (filasActualizadas == 0) {
                    throw new RuntimeException("No se pudo actualizar el stock del producto: " + idProducto);
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
        dto.setEstado(
                factura.getEstado() != null
                        ? factura.getEstado().name()
                        : "PENDIENTE" // o el default que uses
        );
        dto.setObservaciones(factura.getObservaciones());
        dto.setTipoFactura(
                factura.getTipoFactura() != null
                        ? factura.getTipoFactura().name()
                        : null
        );
        dto.setIdCotizacion(factura.getCotizacion() != null ? factura.getCotizacion().getIdCotizacion() : null);
        dto.setFechaCreacion(factura.getFechaCreacion());

        if (factura.getCliente() != null) {
            dto.setNombreCliente(factura.getCliente().getNombre());
            dto.setTelefonoCliente(factura.getCliente().getTelefono());
            dto.setCorreoCliente(factura.getCliente().getCorreo());
        }

        // Convertir productos
        if (factura.getDetalleProductos() != null) {
            dto.setDetalleFactura(factura.getDetalleProductos().stream()
                    .map(det -> {
                        FacturaDTO.DetalleFacturaDTO detalleDto = new FacturaDTO.DetalleFacturaDTO();
                        detalleDto.setIdDetalleFactura(det.getIdDetalle());
                        detalleDto.setIdProducto(det.getProducto().getIdProducto());
                        detalleDto.setCantidad(det.getCantidad());
                        detalleDto.setPrecioUnitario(det.getPrecioUnitario());
                        return detalleDto;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
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
            try {
                Factura.EstadoFactura estadoEnum = Factura.EstadoFactura.valueOf(estado.toUpperCase());
                facturas = facturas.stream()
                        .filter(f -> f.getEstado() == estadoEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Si el estado es inválido, devuelve lista vacía o ignora
                facturas = new ArrayList<>();
            }
        }

        if (tipoFactura != null) {
            try {
                Factura.TipoFactura tipoEnum = Factura.TipoFactura.valueOf(tipoFactura);
                facturas = facturas.stream()
                        .filter(f -> f.getTipoFactura() == tipoEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                facturas = new ArrayList<>();
            }
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
}