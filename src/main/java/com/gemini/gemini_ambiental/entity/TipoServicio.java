package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tipo_servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tipo_servicio", length = 36)
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

    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private String estado = "ACTIVO";

    @Column(name = "activo")
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "icono", length = 50)
    private String icono;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_servicio", nullable = false)
    private CategoriaServicio categoriaServicio;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null || estado.isEmpty()) {
            estado = "ACTIVO";
        }
        if (activo == null) {
            activo = true;
        }
    }
}