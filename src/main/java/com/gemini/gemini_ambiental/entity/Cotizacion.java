package com.gemini.gemini_ambiental.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JoinColumn(name = "dni_cliente", referencedColumnName = "dni", nullable = false)
    private Persona cliente;

    // Empleado asignado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni_empleado", referencedColumnName = "dni")
    private Persona empleado;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_preferida")
    private LocalDate fechaPreferida;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_respuesta")
    private LocalDate fechaRespuesta;

    @Column(name = "estado", nullable = false)
    @Builder.Default
    private String estado = "PENDIENTE";

    @Column(name = "prioridad", length = 50)
    private String prioridad;

    @Column(name = "costo_total_cotizacion", precision = 12, scale = 2, nullable = false)
    private BigDecimal costoTotalCotizacion;

    @Column(name = "valor_servicio", precision = 12, scale = 2)
    private BigDecimal valorServicio;

    @Column(name = "descripcion_problema", columnDefinition = "TEXT")
    private String descripcionProblema;

    @Column(name = "notas_internas", columnDefinition = "TEXT")
    private String notasInternas;

    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<DetalleCotizacion> detalleCotizacion = new ArrayList<>();
}
