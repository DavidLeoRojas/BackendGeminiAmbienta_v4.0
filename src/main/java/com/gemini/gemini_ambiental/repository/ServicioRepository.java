package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, String> { // String para UUID

    // Tus métodos existentes...
    List<Servicio> findByFecha(LocalDate fecha);
    List<Servicio> findByEstado(Servicio.EstadoServicio estado);
    List<Servicio> findByEmpleadoAsignadoDni(String dniEmpleado);
    List<Servicio> findByClienteDni(String dniCliente);
    List<Servicio> findByFechaAndEstado(LocalDate fecha, Servicio.EstadoServicio estado);

    @Query("SELECT s FROM Servicio s WHERE s.fecha >= :startDate AND s.fecha <= :endDate ORDER BY s.fecha, s.hora")
    List<Servicio> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.estado = 'Programado' AND s.fecha = :date")
    Long countServicesByDate(@Param("date") LocalDate date);

    @Query("SELECT s FROM Servicio s JOIN FETCH s.cliente JOIN FETCH s.empleadoAsignado LEFT JOIN FETCH s.cotizacion WHERE s.idServicio = :id")
    Optional<Servicio> findByIdWithRelations(@Param("id") String id);

    // --- ✅ AGREGAR ESTE MÉTODO ---
    @Query("SELECT s FROM Servicio s WHERE s.tipoServicio.idTipoServicio = :idTipoServicio")
    List<Servicio> findByTipoServicio_IdTipoServicio(@Param("idTipoServicio") String idTipoServicio);
    // --- FIN NUEVO MÉTODO ---
}