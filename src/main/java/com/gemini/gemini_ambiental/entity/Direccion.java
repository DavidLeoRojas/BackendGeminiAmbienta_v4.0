package com.gemini.gemini_ambiental.entity;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "Direccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID_direccion", length = 36)
    private String idDireccion;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @Column(name = "descripcion_adicional", length = 255)
    private String descripcionAdicional;

    @ManyToOne
    @JoinColumn(name = "depende_de")
    private Direccion dependeDe;

    @Column(name = "fecha_creacion", updatable = false)
    private java.time.LocalDateTime fechaCreacion = java.time.LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = java.time.LocalDateTime.now();
    }
}