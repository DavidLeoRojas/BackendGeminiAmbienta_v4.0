package com.gemini.gemini_ambiental.service;


import com.gemini.gemini_ambiental.entity.Direccion;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.DireccionRepository;
import com.gemini.gemini_ambiental.dto.DireccionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;

    public List<DireccionDTO> getAllDirecciones() {
        return direccionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DireccionDTO getDireccionById(String id) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada con ID: " + id));
        return convertToDTO(direccion);
    }

    // Otros métodos (create, update, delete) si es necesario

    private DireccionDTO convertToDTO(Direccion direccion) {
        DireccionDTO dto = new DireccionDTO();
        dto.setIdDireccion(direccion.getIdDireccion());
        dto.setNombre(direccion.getNombre());
        dto.setDescripcionAdicional(direccion.getDescripcionAdicional());
        dto.setFechaCreacion(direccion.getFechaCreacion());
        // Mapear depende_de si es necesario
        if (direccion.getDependeDe() != null) {
            dto.setDependeDe(direccion.getDependeDe().getIdDireccion());
        }
        return dto;
    }
}