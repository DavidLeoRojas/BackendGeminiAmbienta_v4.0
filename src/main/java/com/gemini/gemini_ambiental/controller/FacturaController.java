package com.gemini.gemini_ambiental.controller;


import com.gemini.gemini_ambiental.dto.FacturaDTO;
import com.gemini.gemini_ambiental.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @GetMapping
    public ResponseEntity<List<FacturaDTO>> getAllFacturas() {
        List<FacturaDTO> facturas = facturaService.getAllFacturas();
        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FacturaDTO>> searchFacturas(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tipoFactura,
            @RequestParam(required = false) String dniCliente) {
        List<FacturaDTO> facturas = facturaService.searchFacturas(fechaInicio, fechaFin, estado, tipoFactura, dniCliente);
        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaDTO> getFacturaById(@PathVariable String id) {
        FacturaDTO factura = facturaService.getFacturaById(id);
        return ResponseEntity.ok(factura);
    }

    @PostMapping
    public ResponseEntity<FacturaDTO> createFactura(@RequestBody FacturaDTO facturaDTO) {
        FacturaDTO createdFactura = facturaService.createFactura(facturaDTO);
        return ResponseEntity.ok(createdFactura);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaDTO> updateFactura(@PathVariable String id, @RequestBody FacturaDTO facturaDTO) {
        FacturaDTO updatedFactura = facturaService.updateFactura(id, facturaDTO);
        return ResponseEntity.ok(updatedFactura);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactura(@PathVariable String id) {
        facturaService.deleteFactura(id);
        return ResponseEntity.noContent().build();
    }
}