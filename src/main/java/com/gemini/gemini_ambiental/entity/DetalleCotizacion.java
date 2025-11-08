package com.gemini.gemini_ambiental.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_cotizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCotizacion {

    @Id
    @Column(name = "id_detalle_cotizacion", length = 10) // Ajusta la longitud si es necesario
    private String id; // ✅ Cambiado de Long a String

    @ManyToOne
    @JoinColumn(name = "id_tipo_servicio")
    private TipoServicio tipoServicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cotizacion", nullable = false)
    @JsonBackReference
    private Cotizacion cotizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "costos_extra", precision = 12, scale = 2)
    private BigDecimal costosExtra;

    @Column(name = "descripcion_costos_extra", columnDefinition = "TEXT")
    private String descripcionCostosExtra;

    @Column(name = "subtotal", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    public DetalleCotizacion(Cotizacion cotizacion, Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this.cotizacion = cotizacion;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.costosExtra = BigDecimal.ZERO;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}