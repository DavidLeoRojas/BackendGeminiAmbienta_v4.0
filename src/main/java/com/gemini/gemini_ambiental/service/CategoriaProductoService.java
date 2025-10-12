package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.CategoriaProducto;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.CategoriaProductoRepository;
import com.gemini.gemini_ambiental.dto.CategoriaProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoriaProductoService {

    @Autowired
    private CategoriaProductoRepository categoriaProductoRepository;

    public CategoriaProductoDTO createCategoria(CategoriaProductoDTO categoriaDTO) {
        CategoriaProducto categoria = convertToEntity(categoriaDTO);
        CategoriaProducto savedCategoria = categoriaProductoRepository.save(categoria);
        return convertToDTO(savedCategoria);
    }

    public CategoriaProductoDTO updateCategoria(String id, CategoriaProductoDTO categoriaDTO) {
        CategoriaProducto existingCategoria = categoriaProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        // Actualizar campos
        existingCategoria.setNombre(categoriaDTO.getNombre());
        existingCategoria.setDescripcion(categoriaDTO.getDescripcion());

        CategoriaProducto updatedCategoria = categoriaProductoRepository.save(existingCategoria);
        return convertToDTO(updatedCategoria);
    }

    public void deleteCategoria(String id) {
        CategoriaProducto categoria = categoriaProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        categoriaProductoRepository.delete(categoria);
    }

    public CategoriaProductoDTO getCategoriaById(String id) {
        CategoriaProducto categoria = categoriaProductoRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        return convertToDTO(categoria);
    }

    public List<CategoriaProductoDTO> getAllCategorias() {
        return categoriaProductoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CategoriaProductoDTO> searchCategorias(String searchTerm) {
        List<CategoriaProducto> categorias = categoriaProductoRepository.findAll();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            categorias = categorias.stream()
                    .filter(c -> c.getNombre().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return categorias.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CategoriaProducto convertToEntity(CategoriaProductoDTO dto) {
        CategoriaProducto categoria = new CategoriaProducto();
        categoria.setIdCategoriaProducto(dto.getIdCategoriaProducto());
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoria;
    }

    private CategoriaProductoDTO convertToDTO(CategoriaProducto categoria) {
        CategoriaProductoDTO dto = new CategoriaProductoDTO();
        dto.setIdCategoriaProducto(categoria.getIdCategoriaProducto());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setFechaCreacion(categoria.getFechaCreacion());
        return dto;
    }
}