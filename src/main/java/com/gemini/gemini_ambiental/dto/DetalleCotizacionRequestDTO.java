package com.gemini.gemini_ambiental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCotizacionRequestDTO {
    private String idProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}