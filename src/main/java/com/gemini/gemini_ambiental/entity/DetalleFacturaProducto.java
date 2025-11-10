package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_factura_producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleFacturaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_factura", nullable = false)
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Positive(message = "La cantidad debe ser positiva")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @DecimalMin(value = "0.00", message = "El precio unitario debe ser mayor o igual a 0")
    @Column(name = "precio_unitario", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioUnitario;
    @DecimalMin(value = "0.00", message = "El subtotal debe ser mayor o igual a 0")
    @Column(name = "subtotal", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    // ✅ MÉTODO PARA CALCULAR SUBTOTAL AUTOMÁTICAMENTE
    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (this.precioUnitario != null && this.cantidad != null) {
            this.subtotal = this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
        }
    }

    // ✅ MÉTODO PARA OBTENER SUBTOTAL SI ES NULL
    public BigDecimal getSubtotal() {
        if (subtotal == null && precioUnitario != null && cantidad != null) {
            return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
        return subtotal;
    }
}