package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "cargo_especialidad")
public class CargoEspecialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // O puedes usar un String generado manualmente
    private String idCargoEspecialidad; // Cambiado de UUID a String

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_categoria_servicio")
    private CategoriaServicio categoriaServicio;

    // Constructores, getters y setters
    public CargoEspecialidad() {}

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

    public CategoriaServicio getCategoriaServicio() {
        return categoriaServicio;
    }

    public void setCategoriaServicio(CategoriaServicio categoriaServicio) {
        this.categoriaServicio = categoriaServicio;
    }
}