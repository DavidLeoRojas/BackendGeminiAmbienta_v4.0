package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID; // Asegúrate de importar UUID

@Entity
@Table(name = "cargo_especialidad")
public class CargoEspecialidad {

    // Cambiado de String a UUID
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Esto sigue siendo correcto
    private UUID idCargoEspecialidad; // ✅ CORRECTO: Tipo UUID

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY) // Añadido FetchType.LAZY
    @JoinColumn(name = "id_categoria_servicio")
    private CategoriaServicio categoriaServicio;

    // Constructores
    public CargoEspecialidad() {
        // Opcional: Establecer fecha de creación automáticamente al crear la instancia
        // this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public UUID getIdCargoEspecialidad() {
        return idCargoEspecialidad;
    }

    public void setIdCargoEspecialidad(UUID idCargoEspecialidad) {
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