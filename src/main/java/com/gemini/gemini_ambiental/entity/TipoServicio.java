// src/main/java/com/gemini/gemini_ambiental/entity/TipoServicio.java
package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tipo_servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoServicio {

    @Id
    @Column(name = "id_tipo_servicio")
    // No se usa @GeneratedValue aquí porque la DB lo maneja con DEFAULT gen_id_tipo_servicio()
    // Hibernate no generará el ID, lo hará la DB al INSERTAR si no se provee.
    // Si el servicio lo construye sin ID, Hibernate insertará sin ID y la DB lo asignará.
    // Si el servicio lo construye con ID (p. ej. para una actualización), Hibernate lo usará.
    private String idTipoServicio; // <-- Mantener como String

    @Column(name = "nombre_servicio", nullable = false)
    private String nombreServicio;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(precision = 12, scale = 2)
    private BigDecimal costo;

    private String duracion;
    private String frecuencia;
    private String estado;
    private String icono;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria_servicio")
    private CategoriaServicio categoriaServicio;
}