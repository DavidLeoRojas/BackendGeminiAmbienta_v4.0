package com.gemini.gemini_ambiental.controller;



import com.gemini.gemini_ambiental.dto.CategoriaProductoDTO;
import com.gemini.gemini_ambiental.service.CategoriaProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias-producto")
public class CategoriaProductoController {

    @Autowired
    private CategoriaProductoService categoriaProductoService;

    @GetMapping
    public ResponseEntity<List<CategoriaProductoDTO>> getAllCategorias() {
        List<CategoriaProductoDTO> categorias = categoriaProductoService.getAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CategoriaProductoDTO>> searchCategorias(@RequestParam(required = false) String searchTerm) {
        List<CategoriaProductoDTO> categorias = categoriaProductoService.searchCategorias(searchTerm);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProductoDTO> getCategoriaById(@PathVariable String id) {
        CategoriaProductoDTO categoria = categoriaProductoService.getCategoriaById(id);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<CategoriaProductoDTO> createCategoria(@RequestBody CategoriaProductoDTO categoriaDTO) {
        CategoriaProductoDTO createdCategoria = categoriaProductoService.createCategoria(categoriaDTO);
        return ResponseEntity.ok(createdCategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProductoDTO> updateCategoria(@PathVariable String id, @RequestBody CategoriaProductoDTO categoriaDTO) {
        CategoriaProductoDTO updatedCategoria = categoriaProductoService.updateCategoria(id, categoriaDTO);
        return ResponseEntity.ok(updatedCategoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable String id) {
        categoriaProductoService.deleteCategoria(id);
        return ResponseEntity.noContent().build();
    }
}