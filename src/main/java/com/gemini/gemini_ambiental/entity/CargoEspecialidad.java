package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "cargo_especialidad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CargoEspecialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cargo_especialidad")
    private UUID idCargoEspecialidad;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_categoria_servicio")
    private CategoriaServicio categoriaServicio;

    @Column(name = "fecha_creacion", updatable = false)
    @Builder.Default
    private java.time.LocalDateTime fechaCreacion = java.time.LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = java.time.LocalDateTime.now();
    }
}