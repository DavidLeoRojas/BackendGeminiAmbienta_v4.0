package com.gemini.gemini_ambiental.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
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
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id_cotizacion", length = 36, updatable = false)
    private String idCotizacion;

    // --- RELACIÓN CON CLIENTE ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni_cliente", referencedColumnName = "dni", nullable = false)
    @ToString.Exclude
    private Persona cliente;

    @Column(name = "dni_empleado", length = 20)
    private String dniEmpleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni_empleado", referencedColumnName = "dni", insertable = false, updatable = false)
    @ToString.Exclude
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
    private String estado;

    @Column(name = "prioridad", length = 20)
    private String prioridad;

    @Column(name = "costo_total_cotizacion", precision = 12, scale = 2, nullable = false)
    private BigDecimal costoTotalCotizacion;

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

    // --- MÉTODO PARA OBTENER EL DNI DEL CLIENTE ---
    public String getDniCliente() {
        return cliente != null ? cliente.getDni() : null;
    }

    public EstadoCotizacion getEstadoEnum() {
        if (estado == null) {
            return EstadoCotizacion.PENDIENTE;
        }
        try {
            String estadoNormalizado = estado.trim().toUpperCase();
            return EstadoCotizacion.valueOf(estadoNormalizado);
        } catch (IllegalArgumentException e) {
            return EstadoCotizacion.PENDIENTE;
        }
    }

    // Método para agregar detalle
    public void agregarDetalle(DetalleCotizacion detalle) {
        detalle.setCotizacion(this);
        this.detalleCotizacion.add(detalle);
    }

    // Método para calcular costo total desde detalles
    @Transient
    public BigDecimal calcularCostoTotalDesdeDetalles() {
        if (detalleCotizacion == null || detalleCotizacion.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return detalleCotizacion.stream()
                .map(DetalleCotizacion::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public enum EstadoCotizacion {
        PENDIENTE, APROBADA, RECHAZADA, FINALIZADA, CANCELADA_CLIENTE, CANCELADA_EMPRESA
    }
}