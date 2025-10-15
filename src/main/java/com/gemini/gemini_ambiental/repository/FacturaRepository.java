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

    // --- ACTUALIZADO: Incluir detalleFactura y producto ---
    @Query("SELECT f FROM Factura f " +
            "JOIN FETCH f.cliente " +
            "LEFT JOIN FETCH f.cotizacion " +
            "LEFT JOIN FETCH f.detalleFactura df " +
            "LEFT JOIN FETCH df.producto " +
            "WHERE f.idFactura = :id")
    Optional<Factura> findByIdWithRelations(@Param("id") String id);
}