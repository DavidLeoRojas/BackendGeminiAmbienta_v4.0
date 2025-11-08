package com.gemini.gemini_ambiental.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "direccion")
public class Direccion {

    @Id
    @Column(name = "id_direccion", length = 10)
    private String idDireccion;


    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion_adicional")
    private String descripcionAdicional;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depende_de")
    @JsonIgnoreProperties("dependeDe")
    private Direccion dependeDe;

    // Constructores
    public Direccion() {
        // Opcional: Establecer fecha de creación automáticamente al crear la instancia
        // this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Direccion getDependeDe() {
        return dependeDe;
    }

    public void setDependeDe(Direccion dependeDe) {
        this.dependeDe = dependeDe;
    }
}