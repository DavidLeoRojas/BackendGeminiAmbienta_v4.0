package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.CargoEspecialidad;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.CargoEspecialidadRepository;
import com.gemini.gemini_ambiental.dto.CargoEspecialidadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        CargoEspecialidad cargo = cargoEspecialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo no encontrado con ID: " + id));
        return convertToDTO(cargo);
    }

    // Otros métodos (create, update, delete) si es necesario

    private CargoEspecialidadDTO convertToDTO(CargoEspecialidad cargo) {
        CargoEspecialidadDTO dto = new CargoEspecialidadDTO();
        dto.setIdCargoEspecialidad(cargo.getIdCargoEspecialidad());
        dto.setNombre(cargo.getNombre());
        dto.setDescripcion(cargo.getDescripcion());
        dto.setFechaCreacion(cargo.getFechaCreacion());
        // Mapear categoriaServicio si es necesario
        if (cargo.getCategoriaServicio() != null) {
            dto.setIdCategoriaServicio(cargo.getCategoriaServicio().getIdCategoriaServicio());
            dto.setNombreCategoria(cargo.getCategoriaServicio().getNombre()); // Campo adicional para UI
        }
        return dto;
    }
}