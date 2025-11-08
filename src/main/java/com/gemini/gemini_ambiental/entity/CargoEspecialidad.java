package com.gemini.gemini_ambiental.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
// Importa las anotaciones de Hibernate para el tipo JDBC
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "cargo_especialidad")
public class CargoEspecialidad {


    @Id
    @Column(name = "id_cargo_especialidad", length = 10)
    private String idCargoEspecialidad;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_servicio")
    @JsonIgnoreProperties("cargoEspecialidades")
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