package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Long> {

    List<DetalleCotizacion> findByCotizacionIdCotizacion(String idCotizacion);

    @Transactional
    @Modifying
    @Query("DELETE FROM DetalleCotizacion d WHERE d.cotizacion.idCotizacion = :idCotizacion")
    void deleteByCotizacionId(@Param("idCotizacion") String idCotizacion);

    @Query("SELECT d FROM DetalleCotizacion d JOIN FETCH d.producto WHERE d.cotizacion.idCotizacion = :idCotizacion")
    List<DetalleCotizacion> findByCotizacionIdWithProducto(@Param("idCotizacion") String idCotizacion);
}