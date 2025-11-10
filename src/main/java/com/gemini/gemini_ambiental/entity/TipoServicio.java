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

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(precision = 12, scale = 2)
    private BigDecimal precioBase;
}