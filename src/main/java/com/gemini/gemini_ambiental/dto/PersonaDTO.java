package com.gemini.gemini_ambiental.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonaDTO {

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDni;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Número de teléfono inválido")
    private String telefono;

    @Email(message = "Correo electrónico inválido")
    private String correo;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    private String tipoPersona; // Recibido como String desde el frontend

    private String representanteLegal;

    private String nit;

    private String idDireccion;

    private String idCargoEspecialidad;

    private LocalDateTime fechaCreacion;

    // Campos adicionales para mostrar en la UI (sin mapeo directo en la base de datos)
    private String nombreDireccion;
    private String nombreCargo;
}