// En CategoriaServicio.java
package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import lombok.*;
// Importa las anotaciones de Hibernate para el tipo JDBC
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "categoria_servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaServicio {

    @Id
// <-- Añade esta anotación si id_categoria_servicio es UUID en BD
    @Column(name = "id_categoria_servicio", length = 36) // Clarifica el tipo en BD
    private String idCategoriaServicio; // ✅ Tipo String en Java

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
}