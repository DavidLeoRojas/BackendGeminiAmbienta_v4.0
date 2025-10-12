package com.gemini.gemini_ambiental.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionDTO {

    private String idDireccion;
    private String nombre;
    private String descripcionAdicional;
    private String dependeDe;
    private LocalDateTime fechaCreacion;
}