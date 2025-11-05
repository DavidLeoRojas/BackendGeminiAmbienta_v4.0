package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cargo_especialidad")
public class CargoEspecialidad {

    // Cambiado de UUID a String
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Si quieres que JPA genere el ID, sigue usando UUID aquí pero el campo será String
    // O si la base de datos genera el ID como VARCHAR, usa GenerationType.IDENTITY o quita @GeneratedValue
    // Si la base de datos lo maneja como string y no usa auto-incremento, quita @GeneratedValue
    private String idCargoEspecialidad;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "id_categoria_servicio")
    private CategoriaServicio categoriaServicio;

    // Constructores
    public CargoEspecialidad() {
        // Opcional: Establecer fecha de creación automáticamente al crear la instancia
        // this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
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