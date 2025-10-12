package com.gemini.gemini_ambiental.dto;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {

    private String idProducto;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El precio actual es obligatorio")
    private BigDecimal precioActual;

    @NotNull(message = "El stock es obligatorio")
    private Integer stock;

    private String unidadMedida;

    private String idCategoriaProducto;

    private String lote;

    private String proveedor;

    private String observaciones;

    private LocalDateTime fechaCreacion;

    // Campos adicionales para mostrar en la UI
    private String nombreCategoria;
}