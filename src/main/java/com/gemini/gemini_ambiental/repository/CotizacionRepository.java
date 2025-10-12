package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, String> {

    List<Cotizacion> findByEstado(Cotizacion.EstadoCotizacion estado);

    List<Cotizacion> findByClienteDni(String dniCliente);

    List<Cotizacion> findByEmpleadoDni(String dniEmpleado);

    List<Cotizacion> findByFechaSolicitudBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT c FROM Cotizacion c WHERE c.estado = :estado AND c.fechaSolicitud BETWEEN :startDate AND :endDate")
    List<Cotizacion> findByEstadoAndFechaSolicitud(@Param("estado") Cotizacion.EstadoCotizacion estado, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.estado = :estado")
    Long countByEstado(@Param("estado") Cotizacion.EstadoCotizacion estado);

    @Query("SELECT c FROM Cotizacion c JOIN FETCH c.cliente LEFT JOIN FETCH c.empleado WHERE c.idCotizacion = :id")
    Optional<Cotizacion> findByIdWithRelations(@Param("id") String id);
}