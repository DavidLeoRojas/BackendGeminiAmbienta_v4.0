package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Cotizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID_cotizacion", length = 36)
    private String idCotizacion;

    @ManyToOne
    @JoinColumn(name = "DNI_cliente", nullable = false)
    private Persona cliente;

    @ManyToOne
    @JoinColumn(name = "DNI_empleado")
    private Persona empleado;

    @Enumerated(EnumType.STRING) // <-- Esta anotación es clave
    @Column(name = "estado", nullable = false) // <-- Y esta columna también
    private EstadoCotizacion estado = EstadoCotizacion.Pendiente; // <-- Aquí usas el enum

    @NotNull(message = "La fecha de solicitud es obligatoria")
    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_preferida")
    private LocalDate fechaPreferida;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "prioridad", length = 50)
    private String prioridad;

    @Column(name = "descripcion_problema", length = 1000)
    private String descripcionProblema;

    @Column(name = "notas_internas", length = 500)
    private String notasInternas;

    @Column(name = "costo_total_cotizacion", precision = 12, scale = 2)
    private BigDecimal costoTotalCotizacion;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // --- DEFINICIÓN DEL ENUM EstadoCotizacion ---
    // Debe estar DENTRO de la clase Cotizacion
    public enum EstadoCotizacion {
        Pendiente, Aprobada, Rechazada, Finalizada
    }
    // --- FIN DEFINICIÓN ---
}