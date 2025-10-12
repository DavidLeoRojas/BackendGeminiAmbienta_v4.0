package com.gemini.gemini_ambiental.controller;


import com.gemini.gemini_ambiental.dto.TipoServicioDTO;
import com.gemini.gemini_ambiental.service.TipoServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-servicio")
public class TipoServicioController {

    @Autowired
    private TipoServicioService tipoServicioService;

    @GetMapping
    public ResponseEntity<List<TipoServicioDTO>> getAllTiposServicio() {
        try {
            List<TipoServicioDTO> tipos = tipoServicioService.getAllTiposServicio();
            return ResponseEntity.ok(tipos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoServicioDTO> getTipoServicioById(@PathVariable String id) {
        try {
            TipoServicioDTO tipo = tipoServicioService.getTipoServicioById(id);
            return ResponseEntity.ok(tipo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<TipoServicioDTO> createTipoServicio(@RequestBody TipoServicioDTO tipoServicioDTO) {
        try {
            TipoServicioDTO createdTipo = tipoServicioService.createTipoServicio(tipoServicioDTO);
            return ResponseEntity.ok(createdTipo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoServicioDTO> updateTipoServicio(@PathVariable String id, @RequestBody TipoServicioDTO tipoServicioDTO) {
        try {
            TipoServicioDTO updatedTipo = tipoServicioService.updateTipoServicio(id, tipoServicioDTO);
            return ResponseEntity.ok(updatedTipo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoServicio(@PathVariable String id) {
        try {
            tipoServicioService.deleteTipoServicio(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}