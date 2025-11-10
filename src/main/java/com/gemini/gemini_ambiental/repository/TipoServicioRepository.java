package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.TipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TipoServicioRepository extends JpaRepository<TipoServicio, String> {

    // Buscar por categoría
    @Query("SELECT t FROM TipoServicio t WHERE t.categoriaServicio.idCategoriaServicio = :idCategoria")
    List<TipoServicio> findByCategoriaServicio(@Param("idCategoria") String idCategoria);

    // Buscar por estado
    List<TipoServicio> findByEstado(String estado);

    // Buscar por nombre (opcional)
    List<TipoServicio> findByNombreServicioContainingIgnoreCase(String nombre);
}