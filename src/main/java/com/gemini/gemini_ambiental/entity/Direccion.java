package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "direccion")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // O puedes usar un String generado manualmente
    private String idDireccion; // Cambiado de UUID a String

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion_adicional")
    private String descripcionAdicional;

    @ManyToOne
    @JoinColumn(name = "depende_de")
    private Direccion dependeDe;

    // Constructores, getters y setters
    public Direccion() {}

    public String getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(String idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcionAdicional() {
        return descripcionAdicional;
    }

    public void setDescripcionAdicional(String descripcionAdicional) {
        this.descripcionAdicional = descripcionAdicional;
    }

    public Direccion getDependeDe() {
        return dependeDe;
    }

    public void setDependeDe(Direccion dependeDe) {
        this.dependeDe = dependeDe;
    }
}