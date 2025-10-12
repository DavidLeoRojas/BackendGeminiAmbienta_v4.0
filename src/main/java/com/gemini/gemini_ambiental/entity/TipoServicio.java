// src/main/java/com/gemini/gemini_ambiental/entity/TipoServicio.java
package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Tipo_servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID_tipo_servicio", length = 36)
    private String idTipoServicio;

    @Column(name = "nombre_servicio", nullable = false, length = 255)
    private String nombreServicio;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "costo", nullable = false, precision = 12, scale = 2)
    private BigDecimal costo;

    @Column(name = "frecuencia", length = 100)
    private String frecuencia;

    @Column(name = "duracion", length = 100)
    private String duracion;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    // Este es el campo correcto que existe en la DB
    @Column(name = "activo")
    private Boolean activo = true; // El valor por defecto se maneja aquí

    @Column(name = "icono", length = 50)
    private String icono;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_categoria_servicio", nullable = false)
    private CategoriaServicio categoriaServicio;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null || estado.isEmpty()) {
            estado = "ACTIVO";
        }
        // Aseguramos que 'activo' no sea null
        if (activo == null) {
            activo = true;
        }
    }
}