package com.gemini.gemini_ambiental.repository;


import com.gemini.gemini_ambiental.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {

    Optional<Producto> findByNombre(String nombre);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByStockLessThanEqual(int stock);

    List<Producto> findByStockEquals(int stock);

    List<Producto> findByCategoriaProductoIdCategoriaProducto(String idCategoria);

    @Query("SELECT p FROM Producto p WHERE p.stock > 0 ORDER BY p.stock ASC")
    List<Producto> findLowStockProducts();

    // --- MÉTODOS CORREGIDOS ---
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock = 0")
    Long countByStockIsZero(); // Cambiado de countOutofStockProducts

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock > 0 AND p.stock <= 5")
    Long countByStockBetween1And5(); // Cambiado de countLowStockProducts

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock > 5") // Corresponde a > 5
    Long countByStockGreaterThan5(); // Cambiado de countProductosDisponibles

    // --- FIN MÉTODOS CORREGIDOS ---

    @Query("SELECT COUNT(p) FROM Producto p")
    Long countTotalProducts();

    @Query("SELECT p FROM Producto p JOIN FETCH p.categoriaProducto WHERE p.idProducto = :id")
    Optional<Producto> findByIdWithCategory(@Param("id") String id);
}