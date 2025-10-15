package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Detalle_Factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_detalle_factura")
    private Long idDetalleFactura;

    @ManyToOne
    @JoinColumn(name = "ID_factura", nullable = false)
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "ID_producto", nullable = false)
    private Producto producto;

    @Positive(message = "La cantidad debe ser positiva")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @DecimalMin(value = "0.00", message = "El subtotal debe ser mayor o igual a 0")
    @Column(name = "subtotal", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @DecimalMin(value = "0.00", message = "El precio unitario debe ser mayor o igual a 0")
    @Column(name = "precio_unitario", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioUnitario;
}