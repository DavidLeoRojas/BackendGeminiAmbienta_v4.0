package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, String> {

    List<Factura> findByClienteDni(String dniCliente);
    List<Factura> findByEstado(Factura.EstadoFactura estado);
    List<Factura> findByTipoFactura(Factura.TipoFactura tipoFactura);

    @Query("SELECT f FROM Factura f WHERE f.fechaEmision >= :startDate AND f.fechaEmision <= :endDate ORDER BY f.fechaEmision DESC")
    List<Factura> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(f.montoTotal) FROM Factura f WHERE f.estado = 'Pagada'")
    Double sumPaidInvoices();

    @Query("SELECT COUNT(f) FROM Factura f WHERE f.estado = 'Pendiente'")
    Long countPendingInvoices();


    // En FacturaRepository.java - AGREGAR ESTE MÉTODO
    @Query("SELECT f FROM Factura f " +
            "LEFT JOIN FETCH f.detalleProductos dp " +
            "LEFT JOIN FETCH dp.producto " +  // ✅ CARGAR PRODUCTO COMPLETO
            "LEFT JOIN FETCH f.cliente " +
            "LEFT JOIN FETCH f.cotizacion " +
            "WHERE f.idFactura = :id")
    Optional<Factura> findByIdWithProductos(@Param("id") String id);

    @Query("SELECT f.idFactura FROM Factura f WHERE f.idFactura LIKE 'FAC%' ORDER BY f.idFactura DESC LIMIT 1")
    String findLastFacturaId();
    // --- ACTUALIZADO: Incluir detalleFactura y producto ---
    // En FacturaRepository.java
    @Query("SELECT f FROM Factura f " +
            "JOIN FETCH f.cliente " +
            "LEFT JOIN FETCH f.cotizacion " +
            "LEFT JOIN FETCH f.detalleProductos dp " +      // ✅ detalleProductos (no detalleFactura)
            "LEFT JOIN FETCH dp.producto " +                // ✅ producto está en DetalleFacturaProducto
            "LEFT JOIN FETCH f.detalleServicios ds " +      // ✅ también cargar servicios si los necesitas
            "LEFT JOIN FETCH ds.servicio " +
            "WHERE f.idFactura = :id")
    Optional<Factura> findByIdWithRelations(@Param("id") String id);
}