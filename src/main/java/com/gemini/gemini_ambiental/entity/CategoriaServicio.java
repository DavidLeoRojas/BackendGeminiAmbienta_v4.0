// src/main/java/com/gemini/gemini_ambiental/entity/CategoriaServicio.java
package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Categoria_servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID_categoria_servicio", length = 36)
    private String idCategoriaServicio; // ¡Asegúrate de que el nombre sea exactamente este!

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}