// src/main/java/com/gemini/gemini_ambiental/dto/TipoServicioDTO.java
package com.gemini.gemini_ambiental.dto;

import java.math.BigDecimal;

public class TipoServicioDTO {
    private String idTipoServicio;
    private String nombreServicio;
    private String descripcion;
    private BigDecimal costo;
    private String duracion;
    private String frecuencia;
    private String estado;
    private String icono;

    // Este es el campo clave
    private String idCategoriaServicio; // <-- Solo el ID como String

    // Getters y Setters
    public String getIdTipoServicio() { return idTipoServicio; }
    public void setIdTipoServicio(String idTipoServicio) { this.idTipoServicio = idTipoServicio; }

    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public String getIdCategoriaServicio() { return idCategoriaServicio; }
    public void setIdCategoriaServicio(String idCategoriaServicio) { this.idCategoriaServicio = idCategoriaServicio; }
}