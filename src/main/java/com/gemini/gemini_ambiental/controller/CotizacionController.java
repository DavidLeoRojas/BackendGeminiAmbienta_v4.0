package com.gemini.gemini_ambiental.controller;



import com.gemini.gemini_ambiental.dto.CotizacionDTO;
import com.gemini.gemini_ambiental.service.CotizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {

    @Autowired
    private CotizacionService cotizacionService;

    @GetMapping
    public ResponseEntity<List<CotizacionDTO>> getAllCotizaciones() {
        List<CotizacionDTO> cotizaciones = cotizacionService.getAllCotizaciones();
        return ResponseEntity.ok(cotizaciones);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CotizacionDTO>> searchCotizaciones(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String dniCliente,
            @RequestParam(required = false) String dniEmpleado) {
        List<CotizacionDTO> cotizaciones = cotizacionService.searchCotizaciones(fechaInicio, fechaFin, estado, dniCliente, dniEmpleado);
        return ResponseEntity.ok(cotizaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotizacionDTO> getCotizacionById(@PathVariable String id) {
        CotizacionDTO cotizacion = cotizacionService.getCotizacionById(id);
        return ResponseEntity.ok(cotizacion);
    }

    @PostMapping
    public ResponseEntity<CotizacionDTO> createCotizacion(@RequestBody CotizacionDTO cotizacionDTO) {
        CotizacionDTO createdCotizacion = cotizacionService.createCotizacion(cotizacionDTO);
        return ResponseEntity.ok(createdCotizacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CotizacionDTO> updateCotizacion(@PathVariable String id, @RequestBody CotizacionDTO cotizacionDTO) {
        CotizacionDTO updatedCotizacion = cotizacionService.updateCotizacion(id, cotizacionDTO);
        return ResponseEntity.ok(updatedCotizacion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCotizacion(@PathVariable String id) {
        cotizacionService.deleteCotizacion(id);
        return ResponseEntity.noContent().build();
    }
}
