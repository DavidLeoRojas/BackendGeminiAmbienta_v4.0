package com.gemini.gemini_ambiental.service;


import com.gemini.gemini_ambiental.entity.Producto;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.ProductoRepository;
import com.gemini.gemini_ambiental.dto.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public ProductoDTO createProducto(ProductoDTO productoDTO) {
        Producto producto = convertToEntity(productoDTO);
        Producto savedProducto = productoRepository.save(producto);
        return convertToDTO(savedProducto);
    }

    public ProductoDTO updateProducto(String id, ProductoDTO productoDTO) {
        Producto existingProducto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Actualizar campos
        existingProducto.setNombre(productoDTO.getNombre());
        existingProducto.setPrecioActual(productoDTO.getPrecioActual());
        existingProducto.setStock(productoDTO.getStock());
        existingProducto.setUnidadMedida(productoDTO.getUnidadMedida());
        existingProducto.setLote(productoDTO.getLote());
        existingProducto.setProveedor(productoDTO.getProveedor());
        existingProducto.setObservaciones(productoDTO.getObservaciones());

        Producto updatedProducto = productoRepository.save(existingProducto);
        return convertToDTO(updatedProducto);
    }

    public void deleteProducto(String id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        productoRepository.delete(producto);
    }

    public ProductoDTO getProductoById(String id) {
        Producto producto = productoRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return convertToDTO(producto);
    }

    public List<ProductoDTO> getAllProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> searchProductos(String searchTerm, String categoriaId, String estado, String proveedor) {
        List<Producto> productos = productoRepository.findAll();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            productos = productos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(searchTerm.toLowerCase()) ||
                            p.getIdProducto().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (categoriaId != null) {
            productos = productos.stream()
                    .filter(p -> p.getCategoriaProducto() != null && p.getCategoriaProducto().getIdCategoriaProducto().equals(categoriaId))
                    .collect(Collectors.toList());
        }

        if (estado != null) {
            switch (estado) {
                case "disponible":
                    productos = productos.stream()
                            .filter(p -> p.getStock() > 5)
                            .collect(Collectors.toList());
                    break;
                case "bajo":
                    productos = productos.stream()
                            .filter(p -> p.getStock() > 0 && p.getStock() <= 5)
                            .collect(Collectors.toList());
                    break;
                case "agotado":
                    productos = productos.stream()
                            .filter(p -> p.getStock() == 0)
                            .collect(Collectors.toList());
                    break;
            }
        }

        if (proveedor != null) {
            productos = productos.stream()
                    .filter(p -> p.getProveedor() != null && p.getProveedor().equals(proveedor))
                    .collect(Collectors.toList());
        }

        return productos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // --- MÉTODOS CORREGIDOS ---
    public Long countProductosDisponibles() {
        // Corresponde a productos con stock > 5
        return productoRepository.countByStockGreaterThan5();
    }

    public Long countProductosBajoStock() {
        // Corresponde a productos con stock > 0 AND stock <= 5
        return productoRepository.countByStockBetween1And5();
    }

    public Long countProductosAgotados() {
        // Corresponde a productos con stock = 0
        return productoRepository.countByStockIsZero();
    }
    // --- FIN MÉTODOS CORREGIDOS ---

    public Long countTotalProductos() {
        return productoRepository.count();
    }

    private Producto convertToEntity(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        producto.setNombre(dto.getNombre());
        producto.setPrecioActual(dto.getPrecioActual());
        producto.setStock(dto.getStock());
        producto.setUnidadMedida(dto.getUnidadMedida());
        producto.setLote(dto.getLote());
        producto.setProveedor(dto.getProveedor());
        producto.setObservaciones(dto.getObservaciones());
        return producto;
    }

    private ProductoDTO convertToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setPrecioActual(producto.getPrecioActual());
        dto.setStock(producto.getStock());
        dto.setUnidadMedida(producto.getUnidadMedida());
        dto.setIdCategoriaProducto(producto.getCategoriaProducto() != null ? producto.getCategoriaProducto().getIdCategoriaProducto() : null);
        dto.setLote(producto.getLote());
        dto.setProveedor(producto.getProveedor());
        dto.setObservaciones(producto.getObservaciones());
        dto.setFechaCreacion(producto.getFechaCreacion());

        // Agregar datos adicionales para la UI
        if (producto.getCategoriaProducto() != null) {
            dto.setNombreCategoria(producto.getCategoriaProducto().getNombre());
        }

        return dto;
    }
}