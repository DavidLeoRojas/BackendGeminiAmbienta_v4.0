package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cargo_especialidad")
public class CargoEspecialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // O puedes usar un String generado manualmente
    private String idCargoEspecialidad;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    // Campo fechaCreacion
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion; // O Date, Timestamp, etc.

    @ManyToOne
    @JoinColumn(name = "id_categoria_servicio")
    private CategoriaServicio categoriaServicio;

    // Constructores
    public CargoEspecialidad() {
        // Opcional: Establecer fecha de creación automáticamente al crear la instancia
        // this.fechaCreacion = LocalDateTime.now(); // Si usas LocalDateTime
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

    // Getter para fechaCreacion
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    // Setter para fechaCreacion (opcional, dependiendo de si quieres permitir modificarla)
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