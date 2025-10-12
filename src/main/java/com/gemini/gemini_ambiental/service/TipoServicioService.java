// src/main/java/com/gemini/gemini_ambiental/service/TipoServicioService.java
package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.dto.TipoServicioDTO;
import com.gemini.gemini_ambiental.entity.CategoriaServicio;
import com.gemini.gemini_ambiental.entity.TipoServicio;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.CategoriaServicioRepository;
import com.gemini.gemini_ambiental.repository.TipoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoServicioService {

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    @Autowired
    private CategoriaServicioRepository categoriaServicioRepository;

    // Método para obtener todos los tipos de servicio
    public List<TipoServicioDTO> getAllTiposServicio() {
        return tipoServicioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método para obtener un tipo de servicio por ID
    public TipoServicioDTO getTipoServicioById(String id) {
        TipoServicio tipo = tipoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado con ID: " + id));
        return convertToDTO(tipo);
    }

    // Método para crear un nuevo tipo de servicio
    public TipoServicioDTO createTipoServicio(TipoServicioDTO dto) {
        if (dto.getIdCategoriaServicio() == null || dto.getIdCategoriaServicio().isEmpty()) {
            throw new IllegalArgumentException("El ID de la categoría de servicio es obligatorio.");
        }

        CategoriaServicio categoria = categoriaServicioRepository.findById(dto.getIdCategoriaServicio())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de servicio no encontrada con ID: " + dto.getIdCategoriaServicio()));

        TipoServicio tipoServicio = new TipoServicio();
        tipoServicio.setNombreServicio(dto.getNombreServicio());
        tipoServicio.setDescripcion(dto.getDescripcion());
        tipoServicio.setCosto(dto.getCosto());
        tipoServicio.setDuracion(dto.getDuracion());
        tipoServicio.setFrecuencia(dto.getFrecuencia());
        tipoServicio.setEstado(dto.getEstado() != null ? dto.getEstado() : "ACTIVO");
        tipoServicio.setIcono(dto.getIcono());
        tipoServicio.setCategoriaServicio(categoria);

        TipoServicio savedTipoServicio = tipoServicioRepository.save(tipoServicio);
        return convertToDTO(savedTipoServicio);
    }

    // Método para actualizar un tipo de servicio
    public TipoServicioDTO updateTipoServicio(String id, TipoServicioDTO dto) {
        TipoServicio existingTipo = tipoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado con ID: " + id));

        if (dto.getIdCategoriaServicio() != null && !dto.getIdCategoriaServicio().isEmpty()) {
            CategoriaServicio categoria = categoriaServicioRepository.findById(dto.getIdCategoriaServicio())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría de servicio no encontrada con ID: " + dto.getIdCategoriaServicio()));
            existingTipo.setCategoriaServicio(categoria);
        }

        existingTipo.setNombreServicio(dto.getNombreServicio());
        existingTipo.setDescripcion(dto.getDescripcion());
        existingTipo.setCosto(dto.getCosto());
        existingTipo.setDuracion(dto.getDuracion());
        existingTipo.setFrecuencia(dto.getFrecuencia());
        existingTipo.setEstado(dto.getEstado());
        existingTipo.setIcono(dto.getIcono());

        TipoServicio updatedTipo = tipoServicioRepository.save(existingTipo);
        return convertToDTO(updatedTipo);
    }

    // Método para eliminar un tipo de servicio
    public void deleteTipoServicio(String id) {
        if (!tipoServicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de servicio no encontrado con ID: " + id);
        }
        tipoServicioRepository.deleteById(id);
    }

    // Método auxiliar para convertir entidad a DTO
    private TipoServicioDTO convertToDTO(TipoServicio tipoServicio) {
        TipoServicioDTO dto = new TipoServicioDTO();
        dto.setIdTipoServicio(tipoServicio.getIdTipoServicio());
        dto.setNombreServicio(tipoServicio.getNombreServicio());
        dto.setDescripcion(tipoServicio.getDescripcion());
        dto.setCosto(tipoServicio.getCosto());
        dto.setDuracion(tipoServicio.getDuracion());
        dto.setFrecuencia(tipoServicio.getFrecuencia());
        dto.setEstado(tipoServicio.getEstado());
        dto.setIcono(tipoServicio.getIcono());
        dto.setIdCategoriaServicio(tipoServicio.getCategoriaServicio().getIdCategoriaServicio());
        return dto;
    }
}