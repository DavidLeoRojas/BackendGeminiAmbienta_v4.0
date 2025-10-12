// src/main/java/com/gemini/gemini_ambiental/service/CategoriaServicioService.java
package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.CategoriaServicio;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.CategoriaServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServicioService {

    @Autowired
    private CategoriaServicioRepository categoriaServicioRepository;

    public List<CategoriaServicio> getAllCategoriasServicio() {
        return categoriaServicioRepository.findAll();
    }

    public CategoriaServicio createCategoriaServicio(CategoriaServicio categoriaServicio) {
        return categoriaServicioRepository.save(categoriaServicio);
    }

    public CategoriaServicio getCategoriaServicioById(String id) {
        return categoriaServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de servicio no encontrada con ID: " + id));
    }
}