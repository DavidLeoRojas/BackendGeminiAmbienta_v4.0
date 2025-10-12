package com.gemini.gemini_ambiental.dto;


import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CargoEspecialidadDTO {

    private String idCargoEspecialidad;
    private String nombre;
    private String descripcion;
    private String idCategoriaServicio;
    private LocalDateTime fechaCreacion;
    private String nombreCategoria; // Para mostrar en la UI
}