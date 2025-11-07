package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import lombok.*;

// No necesitas importar LocalDateTime si no lo usas, pero lo dejo por si acaso
import java.time.LocalDateTime;

@Entity
@Table(name = "categoria_servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_categoria_servicio", length = 36)
    private String idCategoriaServicio;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;


}