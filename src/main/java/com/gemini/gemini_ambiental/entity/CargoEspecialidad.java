package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
// Importa las anotaciones de Hibernate para el tipo JDBC
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "cargo_especialidad")
public class CargoEspecialidad {

    // Cambiado de UUID a String, pero se mapea como UUID en BD
    @Id
    // @GeneratedValue(strategy = GenerationType.UUID) // Comentado si lo defines manualmente o la BD lo genera
    @JdbcTypeCode(SqlTypes.UUID) // <-- Añade esta anotación
    @Column(name = "id_cargo_especialidad", updatable = false, nullable = false, columnDefinition = "uuid") // Clarifica el tipo en BD
    private String idCargoEspecialidad; // ✅ Tipo String en Java

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY) // Añadido fetch = FetchType.LAZY
    @JoinColumn(name = "id_categoria_servicio")
    private CategoriaServicio categoriaServicio;

    // Constructores, Getters y Setters (manteniendo idCargoEspecialidad como String)
    public CargoEspecialidad() {
    }

    public String getIdCargoEspecialidad() {
        return idCargoEspecialidad;
    }

    public void setIdCargoEspecialidad(String idCargoEspecialidad) {
        this.idCargoEspecialidad = idCargoEspecialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public CategoriaServicio getCategoriaServicio() {
        return categoriaServicio;
    }

    public void setCategoriaServicio(CategoriaServicio categoriaServicio) {
        this.categoriaServicio = categoriaServicio;
    }
}