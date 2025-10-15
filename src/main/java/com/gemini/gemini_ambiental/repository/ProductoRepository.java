package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock = 0")
    Long countByStockIsZero();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock > 0 AND p.stock <= 5")
    Long countByStockBetween1And5();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock > 5")
    Long countByStockGreaterThan5();

    @Query("SELECT COUNT(p) FROM Producto p")
    Long countTotalProducts();

    @Query("SELECT p FROM Producto p JOIN FETCH p.categoriaProducto WHERE p.idProducto = :id")
    Optional<Producto> findByIdWithCategory(@Param("id") String id);

    // --- MÉTODO PARA RESTAR STOCK ---
    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.stock = p.stock - :cantidad WHERE p.idProducto = :id AND p.stock >= :cantidad")
    int restarStock(@Param("id") String idProducto, @Param("cantidad") Integer cantidad);
}