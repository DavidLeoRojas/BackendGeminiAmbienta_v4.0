package com.gemini.gemini_ambiental.dto;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacturaDTO {

    private String idFactura;

    private String dniCliente;

    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate fechaEmision;

    @NotNull(message = "El monto total es obligatorio")
    private BigDecimal montoTotal;

    private String estado;

    private String observaciones;

    private String tipoFactura;

    private String idCotizacion;

    private LocalDateTime fechaCreacion;

    // Campos adicionales para mostrar en la UI
    private String nombreCliente;
    private String telefonoCliente;
    private String correoCliente;
}