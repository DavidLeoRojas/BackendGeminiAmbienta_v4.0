package com.gemini.gemini_ambiental.controller;


import com.gemini.gemini_ambiental.dto.ProductoDTO;
import com.gemini.gemini_ambiental.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        List<ProductoDTO> productos = productoService.getAllProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductoDTO>> searchProductos(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String categoriaId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String proveedor) {
        List<ProductoDTO> productos = productoService.searchProductos(searchTerm, categoriaId, estado, proveedor);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable String id) {
        ProductoDTO producto = productoService.getProductoById(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> createProducto(@RequestBody ProductoDTO productoDTO) {
        ProductoDTO createdProducto = productoService.createProducto(productoDTO);
        return ResponseEntity.ok(createdProducto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> updateProducto(@PathVariable String id, @RequestBody ProductoDTO productoDTO) {
        ProductoDTO updatedProducto = productoService.updateProducto(id, productoDTO);
        return ResponseEntity.ok(updatedProducto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable String id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<ProductStats> getProductStats() {
        ProductStats stats = new ProductStats(
                productoService.countProductosDisponibles(),
                productoService.countProductosBajoStock(),
                productoService.countProductosAgotados(),
                productoService.countTotalProductos()
        );
        return ResponseEntity.ok(stats);
    }

    // Clase interna para las estadísticas de productos
    public static class ProductStats {
        private Long disponibles;
        private Long bajoStock;
        private Long agotados;
        private Long total;

        public ProductStats(Long disponibles, Long bajoStock, Long agotados, Long total) {
            this.disponibles = disponibles;
            this.bajoStock = bajoStock;
            this.agotados = agotados;
            this.total = total;
        }

        // Getters
        public Long getDisponibles() { return disponibles; }
        public Long getBajoStock() { return bajoStock; }
        public Long getAgotados() { return agotados; }
        public Long getTotal() { return total; }
    }
}
