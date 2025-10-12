package com.gemini.gemini_ambiental.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "Cargo_Especialidad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CargoEspecialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID_cargo_especialidad", length = 36)
    private String idCargoEspecialidad;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "ID_categoria_servicio")
    private CategoriaServicio categoriaServicio;

    @Column(name = "fecha_creacion", updatable = false)
    private java.time.LocalDateTime fechaCreacion = java.time.LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = java.time.LocalDateTime.now();
    }
}