package com.gemini.gemini_ambiental.dto;



import jakarta.validation.constraints.NotBlank;
import lombok.*; // Importante para @Data, @Getter, @Setter, etc.

import java.time.LocalDateTime;

// Usando @Data de Lombok para generar getters, setters, toString, equals, hashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaProductoDTO {

    private String idCategoriaProducto;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre; // <-- Debe existir este campo

    private String descripcion;

    private LocalDateTime fechaCreacion;
}