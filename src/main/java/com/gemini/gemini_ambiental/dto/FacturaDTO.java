package com.gemini.gemini_ambiental.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FacturaDTO {

    private String idFactura;
    private String dniCliente;
    private String nombreCliente;
    private String telefonoCliente;
    private String correoCliente;
    private LocalDate fechaEmision;
    private BigDecimal montoTotal;
    private String estado;
    private String observaciones;
    private String tipoFactura;
    private String idCotizacion;
    private LocalDateTime fechaCreacion;
    private BigDecimal valorServicio;

    // ✅ CORREGIDO: Agregar objeto cliente completo
    private PersonaDTO cliente;

    // ✅ CORREGIDO: Agregar objeto cotizacion
    private CotizacionDTO cotizacion;

    // ✅ CORREGIDO: Mantener detalle unificado PERO con información completa
    private List<DetalleFacturaDTO> detalleFactura;

    // ✅ NUEVO: Para compatibilidad con frontend existente
    private List<DetalleFacturaDTO> detalleProductos;
    private List<DetalleFacturaDTO> detalleServicios;

    @Data
    public static class DetalleFacturaDTO {
        private Long idDetalleFactura;
        private String idProducto;
        private String idServicio; // ✅ NUEVO: Para servicios
        private Integer cantidad;
        private BigDecimal subtotal;
        private BigDecimal precioUnitario;
        private String nombreProducto;
        private String nombreServicio; // ✅ NUEVO: Para servicios
        private Integer stockProducto;
        private String tipo; // ✅ NUEVO: "producto" o "servicio"
    }
}