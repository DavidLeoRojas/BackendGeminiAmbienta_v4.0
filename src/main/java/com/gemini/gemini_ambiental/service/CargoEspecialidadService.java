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

    public CargoEspecialidadDTO getCargoById(String id) { // El ID entra como String
        // No necesitas convertir a UUID
        CargoEspecialidad cargo = cargoEspecialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo no encontrado con ID: " + id));
        return convertToDTO(cargo);
    }

    // Otros métodos (create, update, delete) aquí...

    private CargoEspecialidadDTO convertToDTO(CargoEspecialidad cargo) {
        CargoEspecialidadDTO dto = new CargoEspecialidadDTO();
        // El ID ya es String, no necesitas toString()
        dto.setIdCargoEspecialidad(cargo.getIdCargoEspecialidad());
        dto.setNombre(cargo.getNombre());
        dto.setDescripcion(cargo.getDescripcion());
        dto.setFechaCreacion(cargo.getFechaCreacion()); // Asegúrate de que CargoEspecialidad tenga este campo y getter
        if (cargo.getCategoriaServicio() != null) {
            // El ID ya es String, no necesitas toString()
            dto.setIdCategoriaServicio(cargo.getCategoriaServicio().getIdCategoriaServicio());
            dto.setNombreCategoria(cargo.getCategoriaServicio().getNombre());
        }
        return dto;
    }
}