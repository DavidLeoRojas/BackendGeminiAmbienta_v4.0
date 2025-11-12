package com.gemini.gemini_ambiental.dto;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicioDTO {

    private String idServicio;

    // Cotización opcional
    private String idCotizacion;

    private String dniEmpleadoAsignado;
    private String dniCliente;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    private String duracionEstimada;
    private String observaciones;
    private String prioridad;
    private String estado;

    // Si no envías activo, será true
    private Boolean activo = true;

    // Por defecto, NO tiene cotización
    private Boolean servicioSinCotizacion = false;

    private String idTipoServicio;
    private LocalDateTime fechaCreacion;

    // Info para la UI
    private String nombreCliente;
    private String telefonoCliente;
    private String correoCliente;
    private String nombreEmpleado;
    private String telefonoEmpleado;
    private String correoEmpleado;
}
