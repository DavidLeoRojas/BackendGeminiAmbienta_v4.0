package com.gemini.gemini_ambiental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionRequestDTO {
    private String dniCliente;
    private LocalDate fechaPreferida;
    private String prioridad;
    private BigDecimal costoTotalCotizacion;
    private String descripcionProblema;
    private String notasInternas;
    private String estado;
    private List<DetalleCotizacionRequestDTO> detalleCotizacion;

}