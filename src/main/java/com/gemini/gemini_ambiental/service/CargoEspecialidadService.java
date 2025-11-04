package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.CargoEspecialidad;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.CargoEspecialidadRepository;
import com.gemini.gemini_ambiental.dto.CargoEspecialidadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CargoEspecialidadService {

    @Autowired
    private CargoEspecialidadRepository cargoEspecialidadRepository;

    public List<CargoEspecialidadDTO> getAllCargos() {
        return cargoEspecialidadRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CargoEspecialidadDTO getCargoById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ResourceNotFoundException("ID de cargo no proporcionado");
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("ID inválido: debe ser un UUID válido");
        }
        CargoEspecialidad cargo = cargoEspecialidadRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo no encontrado con ID: " + id));
        return convertToDTO(cargo);
    }

    private CargoEspecialidadDTO convertToDTO(CargoEspecialidad cargo) {
        CargoEspecialidadDTO dto = new CargoEspecialidadDTO();
        dto.setIdCargoEspecialidad(cargo.getIdCargoEspecialidad().toString());
        dto.setNombre(cargo.getNombre());
        dto.setDescripcion(cargo.getDescripcion());
        dto.setFechaCreacion(cargo.getFechaCreacion());
        if (cargo.getCategoriaServicio() != null) {
            dto.setIdCategoriaServicio(cargo.getCategoriaServicio().getIdCategoriaServicio().toString());
            dto.setNombreCategoria(cargo.getCategoriaServicio().getNombre());
        }
        return dto;
    }
}