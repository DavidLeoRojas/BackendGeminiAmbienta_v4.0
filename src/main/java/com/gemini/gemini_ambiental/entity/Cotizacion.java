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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dni_cliente", nullable = false) // ✅ Agregado nullable = false
    @JsonIgnoreProperties({"direccion", "cargoEspecialidad", "password"})
    private Persona cliente;

    // Empleado asignado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dni_empleado") // ✅ Correcto
    @JsonIgnoreProperties({"direccion", "cargoEspecialidad", "password"})
    private Persona empleado;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "fecha_solicitud", nullable = false) // ✅ Correcto
    private LocalDateTime fechaSolicitud;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_preferida") // ✅ Agregado name
    private LocalDate fechaPreferida;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_respuesta") // ✅ Agregado name
    private LocalDate fechaRespuesta;

    // ✅ CORREGIDO: Usar Enum y mapeo correcto
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 50)
    @Builder.Default
    private EstadoCotizacion estado = EstadoCotizacion.PENDIENTE;

    @Column(name = "prioridad") // ✅ Agregado name
    private String prioridad;

    @Column(name = "costo_total_cotizacion", precision = 12, scale = 2) // ✅ Agregado name, removido nullable = false
    private BigDecimal costoTotalCotizacion;

    @Column(name = "valor_servicio", precision = 12, scale = 2) // ✅ Agregado name
    private BigDecimal valorServicio;

    @Column(name = "descripcion_problema", columnDefinition = "TEXT") // ✅ Agregado name
    private String descripcionProblema;

    @Column(name = "notas_internas", columnDefinition = "TEXT") // ✅ Agregado name
    private String notasInternas;

    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("cotizacion")
    private List<DetalleCotizacion> detalleCotizacion = new ArrayList<>();

    // ✅ AGREGAR este Enum
    public enum EstadoCotizacion {
        PENDIENTE, APROBADA, RECHAZADA, FINALIZADA
    }
}