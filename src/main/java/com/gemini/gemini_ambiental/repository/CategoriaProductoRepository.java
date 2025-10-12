package com.gemini.gemini_ambiental.repository;


import com.gemini.gemini_ambiental.entity.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, String> {

    Optional<CategoriaProducto> findByNombre(String nombre);

    List<CategoriaProducto> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT c FROM CategoriaProducto c WHERE c.idCategoriaProducto = :id")
    Optional<CategoriaProducto> findByIdWithProducts(@Param("id") String id);
}