package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @Column(name = "DNI", length = 20)
    private String dni; // ← Clave primaria

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Column(name = "tipo_dni", length = 50, nullable = false)
    private String tipoDni;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Número de teléfono inválido")
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Email(message = "Correo electrónico inválido")
    @Column(name = "correo", length = 255, unique = true)
    private String correo;

    @NotBlank(message = "El rol es obligatorio")
    @Column(name = "rol", length = 50, nullable = false)
    private String rol;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_persona", length = 50, nullable = false)
    private TipoPersona tipoPersona = TipoPersona.Natural;

    @Column(name = "representante_legal", length = 20)
    private String representanteLegal;

    @Column(name = "nit", length = 20)
    private String nit;

    @Column(name = "password", length = 255) // 🔐
    private String password;

    @ManyToOne
    @JoinColumn(name = "ID_direccion")
    private Direccion direccion;

    @ManyToOne
    @JoinColumn(name = "ID_cargo_especialidad")
    private CargoEspecialidad cargoEspecialidad;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public enum TipoPersona {
        Natural, Juridica
    }
}