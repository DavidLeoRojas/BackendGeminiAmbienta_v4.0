package com.gemini.gemini_ambiental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionDTO {
    private String idCotizacion;
    private String dniCliente;
    private String dniEmpleado;
    private String nombreCliente;
    private String telefonoCliente;
    private String correoCliente;
    private String nombreEmpleado;
    private String telefonoEmpleado;
    private String correoEmpleado;
    private LocalDateTime fechaSolicitud;
    private LocalDate fechaPreferida;
    private LocalDate fechaRespuesta;
    private String estado;
    private String prioridad;
    private BigDecimal costoTotalCotizacion;
    private String descripcionProblema;
    private String notasInternas;
    private LocalDateTime fechaCreacion;
    private List<DetalleCotizacionDTO> detalleCotizacion;
}