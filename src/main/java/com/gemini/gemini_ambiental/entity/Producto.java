package com.gemini.gemini_ambiental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "producto") // <-- Cambiado a minúsculas
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Asumiendo UUID
    @Column(name = "id_producto", length = 36)
    private String idProducto; // <-- Ahora es String

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @NotNull(message = "El precio actual es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio debe ser mayor o igual a 0")
    @Column(name = "precio_actual", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioActual;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_producto")
    private CategoriaProducto categoriaProducto;

    @Column(name = "lote", length = 50)
    private String lote;

    @Column(name = "proveedor", length = 255)
    private String proveedor;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Builder.Default
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}