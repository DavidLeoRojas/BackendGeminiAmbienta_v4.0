package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "Servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // ✅ Esto es correcto
    @Column(name = "ID_servicio", length = 36, updatable = false) // updatable = false es opcional pero seguro
    private String idServicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_cotizacion")
    private Cotizacion cotizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DNI_empleado_asignado", referencedColumnName = "dni")
    private Persona empleadoAsignado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DNI_cliente", nullable = false, referencedColumnName = "dni")
    private Persona cliente;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede ser pasada")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Column(name = "duracion_estimada", length = 100)
    private String duracionEstimada;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "prioridad", length = 50)
    private String prioridad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoServicio estado = EstadoServicio.Programado;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "servicio_sin_cotizacion", nullable = false)
    private Boolean servicioSinCotizacion = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_servicio")
    private TipoServicio tipoServicio;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public enum EstadoServicio {
        Programado, EnProgreso, Completado, Cancelado
    }
}