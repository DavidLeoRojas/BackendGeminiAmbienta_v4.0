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
    private String idTipoServicio;

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