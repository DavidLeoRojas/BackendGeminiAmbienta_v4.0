package com.gemini.gemini_ambiental.controller;



import com.gemini.gemini_ambiental.dto.DireccionDTO;
import com.gemini.gemini_ambiental.service.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    @GetMapping
    public ResponseEntity<List<DireccionDTO>> getAllDirecciones() {
        List<DireccionDTO> direcciones = direccionService.getAllDirecciones();
        return ResponseEntity.ok(direcciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DireccionDTO> getDireccionById(@PathVariable String id) {
        DireccionDTO direccion = direccionService.getDireccionById(id);
        return ResponseEntity.ok(direccion);
    }

    // Otros métodos (POST, PUT, DELETE) si es necesario
}