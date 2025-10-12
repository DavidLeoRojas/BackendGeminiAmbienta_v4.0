package com.gemini.gemini_ambiental.controller;


import com.gemini.gemini_ambiental.dto.CargoEspecialidadDTO;
import com.gemini.gemini_ambiental.service.CargoEspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargos")
public class CargoEspecialidadController {

    @Autowired
    private CargoEspecialidadService cargoService;

    @GetMapping
    public ResponseEntity<List<CargoEspecialidadDTO>> getAllCargos() {
        List<CargoEspecialidadDTO> cargos = cargoService.getAllCargos();
        return ResponseEntity.ok(cargos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoEspecialidadDTO> getCargoById(@PathVariable String id) {
        CargoEspecialidadDTO cargo = cargoService.getCargoById(id);
        return ResponseEntity.ok(cargo);
    }

    // Otros métodos (POST, PUT, DELETE) si es necesario
}