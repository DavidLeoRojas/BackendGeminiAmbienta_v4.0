
package com.gemini.gemini_ambiental.controller;

import com.gemini.gemini_ambiental.entity.CategoriaServicio;
import com.gemini.gemini_ambiental.service.CategoriaServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias-servicio")
public class CategoriaServicioController {

    @Autowired
    private CategoriaServicioService categoriaServicioService;

    // GET: Obtener todas las categorías de servicio
    @GetMapping
    public ResponseEntity<List<CategoriaServicio>> getAllCategoriasServicio() {
        List<CategoriaServicio> categorias = categoriaServicioService.getAllCategoriasServicio();
        return ResponseEntity.ok(categorias);
    }

    // POST: Crear una nueva categoría de servicio (opcional, si necesitas agregarlas desde el frontend)
    @PostMapping
    public ResponseEntity<CategoriaServicio> createCategoriaServicio(@RequestBody CategoriaServicio categoriaServicio) {
        CategoriaServicio nuevaCategoria = categoriaServicioService.createCategoriaServicio(categoriaServicio);
        return ResponseEntity.ok(nuevaCategoria);
    }

    // GET: Obtener una categoría por ID (opcional, para detalles)
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaServicio> getCategoriaServicioById(@PathVariable String id) {
        CategoriaServicio categoria = categoriaServicioService.getCategoriaServicioById(id);
        return ResponseEntity.ok(categoria);
    }
}