package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, String> {

    // Encontrar por ID con todas las relaciones
    @Query("SELECT c FROM Cotizacion c LEFT JOIN FETCH c.detalleCotizacion d LEFT JOIN FETCH d.producto WHERE c.idCotizacion = :id")
    Optional<Cotizacion> findByIdWithAllRelations(@Param("id") String id);

    // Encontrar todas las cotizaciones básicas (sin detalles)
    @Query("SELECT c FROM Cotizacion c LEFT JOIN FETCH c.cliente LEFT JOIN FETCH c.empleado")
    List<Cotizacion> findAllBasic();

    // Encontrar cotizaciones por cliente
    @Query("SELECT c FROM Cotizacion c LEFT JOIN FETCH c.cliente WHERE c.cliente.dni = :dniCliente")
    List<Cotizacion> findByClienteDni(@Param("dniCliente") String dniCliente);

    // Encontrar cotizaciones por estado
    List<Cotizacion> findByEstado(String estado);

    // Encontrar cotizaciones por prioridad
    List<Cotizacion> findByPrioridad(String prioridad);

    // Contar cotizaciones por estado
    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.estado = :estado")
    Long countByEstado(@Param("estado") String estado);
}