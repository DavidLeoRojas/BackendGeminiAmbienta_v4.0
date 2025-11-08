package com.gemini.gemini_ambiental.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @Column(name = "dni", length = 20)
    private String dni;

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
    @Column(name = "tipo_persona", length = 10, nullable = false)
    @Builder.Default
    private TipoPersona tipoPersona = TipoPersona.Natural;

    @Column(name = "representante_legal", length = 20)
    private String representanteLegal;

    @Column(name = "nit", length = 20)
    private String nit;

    @Column(name = "password", length = 255)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_direccion")
    @JsonIgnoreProperties({"persona", "dependencia"})
    private Direccion direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cargo_especialidad")
    @JsonIgnoreProperties("personas")
    private CargoEspecialidad cargoEspecialidad;

    public enum TipoPersona {
        Natural, Juridica
    }
}
