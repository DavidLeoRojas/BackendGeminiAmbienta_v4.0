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

    // --- NUEVO: Campo valorServicio ---
    private BigDecimal valorServicio;

    // --- NUEVO: Detalle del DTO ---
    private List<DetalleFacturaDTO> detalleFactura;

    // DTO para el detalle
    @Data
    public static class DetalleFacturaDTO {
        private Long idDetalleFactura;
        private String idProducto;
        private Integer cantidad;
        private BigDecimal subtotal;
        private BigDecimal precioUnitario;
    }
}