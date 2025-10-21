package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "Factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID_factura", length = 36)
    private String idFactura;

    @ManyToOne
    @JoinColumn(name = "DNI_cliente", nullable = false)
    private Persona cliente;

    @NotNull(message = "La fecha de emisión es obligatoria")
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.00", message = "El monto debe ser mayor o igual a 0")
    @Column(name = "monto_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoFactura estado = EstadoFactura.Pendiente;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_factura")
    private TipoFactura tipoFactura = TipoFactura.Simple;

    @ManyToOne
    @JoinColumn(name = "ID_cotizacion")
    private Cotizacion cotizacion;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleFactura> detalleFactura = new ArrayList<>();

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "valor_servicio", precision = 12, scale = 2)
    private BigDecimal valorServicio;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public void addDetalleFactura(DetalleFactura detalle) {
        detalleFactura.add(detalle);
        detalle.setFactura(this);
    }

    public void removeDetalleFactura(DetalleFactura detalle) {
        detalleFactura.remove(detalle);
        detalle.setFactura(null);
    }

    public BigDecimal getValorServicio() {
        return valorServicio;
    }

    public void setValorServicio(BigDecimal valorServicio) {
        this.valorServicio = valorServicio;
    }

    public enum EstadoFactura {
        Pendiente, Pagada, Vencida, Rechazada
    }

    public enum TipoFactura {
        Simple, ConCotizacion
    }
}