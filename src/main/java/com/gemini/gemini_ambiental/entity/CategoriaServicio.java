package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import lombok.*;
// No se importan anotaciones de JdbcTypeCode ni SqlTypes
// import org.hibernate.annotations.JdbcTypeCode;
// import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "categoria_servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaServicio {

    @Id
    // No se usa @JdbcTypeCode ni se define como UUID
    @Column(name = "id_categoria_servicio", length = 36) // Ajusta la longitud si es necesario, p. ej. si es un código como CAT001
    private String idCategoriaServicio; // ✅ Tipo String en Java

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    // ⭐️ CORREGIDO: ELIMINADO 'fecha_creacion' porque no existe en la tabla 'categoria_servicio'
    // @Column(name = "fecha_creacion")
    // private LocalDateTime fechaCreacion;

    // Constructores, getters y setters...
    // Asegúrate de que getIdCategoriaServicio() devuelva String
    public String getIdCategoriaServicio() {
        return idCategoriaServicio;
    }

    public void setIdCategoriaServicio(String idCategoriaServicio) {
        this.idCategoriaServicio = idCategoriaServicio;
    }

    // ... otros getters y setters ...
}