package com.gemini.gemini_ambiental.dto;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CotizacionDTO {

    private String idCotizacion;

    private String dniCliente;

    private String dniEmpleado;

    private String estado;

    @NotNull(message = "La fecha de solicitud es obligatoria")
    private LocalDateTime fechaSolicitud;

    private LocalDate fechaPreferida;

    private LocalDateTime fechaRespuesta;

    private String prioridad;

    private String descripcionProblema;

    private String notasInternas;

    private BigDecimal costoTotalCotizacion;

    private LocalDateTime fechaCreacion;

    // Campos adicionales para mostrar en la UI
    private String nombreCliente;
    private String telefonoCliente;
    private String correoCliente;
    private String nombreEmpleado;
    private String telefonoEmpleado;
    private String correoEmpleado;
}