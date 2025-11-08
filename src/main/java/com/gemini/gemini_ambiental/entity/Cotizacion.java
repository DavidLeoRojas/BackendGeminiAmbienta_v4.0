package com.gemini.gemini_ambiental.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "cotizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cotizacion {

    @Id
    @Column(name = "id_cotizacion", length = 36, updatable = false)
    private String idCotizacion;

    // Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni_cliente")
    @JsonIgnoreProperties({"direccion", "cargoEspecialidad", "password"})
    private Persona cliente;

    // Empleado asignado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni_empleado")
    @JsonIgnoreProperties({"direccion", "cargoEspecialidad", "password"})
    private Persona empleado;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPreferida;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRespuesta;

    @Column(nullable = false)
    @Builder.Default
    private String estado = "PENDIENTE";

    private String prioridad;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal costoTotalCotizacion;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorServicio;

    @Column(columnDefinition = "TEXT")
    private String descripcionProblema;

    @Column(columnDefinition = "TEXT")
    private String notasInternas;

    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("cotizacion")
    private List<DetalleCotizacion> detalleCotizacion = new ArrayList<>();
}
