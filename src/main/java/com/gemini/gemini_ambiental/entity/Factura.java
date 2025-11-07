package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura {

    @Id
    @Column(name = "id_factura", length = 36)
    private String idFactura;

    @ManyToOne
    @JoinColumn(name = "dni_cliente", nullable = false)
    private Persona cliente;

    @NotNull(message = "La fecha de emisión es obligatoria")
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.00", message = "El monto debe ser mayor o igual a 0")
    @Column(name = "monto_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoFactura estado = EstadoFactura.PENDIENTE;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_factura", nullable = false, length = 20)
    @Builder.Default
    private TipoFactura tipoFactura = TipoFactura.Simple;

    @ManyToOne
    @JoinColumn(name = "id_cotizacion")
    private Cotizacion cotizacion;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetalleFactura> detalleServicios = new ArrayList<>();

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetalleFacturaProducto> detalleProductos = new ArrayList<>();

    @Column(name = "fecha_creacion", updatable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "valor_servicio", precision = 12, scale = 2)
    private BigDecimal valorServicio;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public void addDetalleServicio(DetalleFactura detalle) {
        detalleServicios.add(detalle);
        detalle.setFactura(this);
    }

    public void addDetalleProducto(DetalleFacturaProducto detalle) {
        detalleProductos.add(detalle);
        detalle.setFactura(this);
    }

    public enum EstadoFactura {
        PENDIENTE, PAGADA, VENCIDA, RECHAZADA
    }

    public enum TipoFactura {
        Simple, ConCotizacion
    }
}