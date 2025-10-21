package com.gemini.gemini_ambiental.controller;

import com.gemini.gemini_ambiental.dto.CotizacionDTO;
import com.gemini.gemini_ambiental.dto.CotizacionRequestDTO;
import com.gemini.gemini_ambiental.service.CotizacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cotizaciones")
@CrossOrigin(origins = "*")
public class CotizacionController {

    @Autowired
    private CotizacionService cotizacionService;

    // ========== ENDPOINTS DE CONSULTA ==========

    @GetMapping
    public ResponseEntity<List<CotizacionDTO>> getAllCotizaciones() {
        try {
            List<CotizacionDTO> cotizaciones = cotizacionService.getAllCotizaciones();
            return ResponseEntity.ok(cotizaciones);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotizacionDTO> getCotizacionById(@PathVariable String id) {
        try {
            CotizacionDTO cotizacion = cotizacionService.getCotizacionById(id);
            return ResponseEntity.ok(cotizacion);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/completa")
    public ResponseEntity<CotizacionDTO> getCotizacionCompleta(@PathVariable String id) {
        try {
            CotizacionDTO cotizacion = cotizacionService.getCotizacionByIdWithDetails(id);
            return ResponseEntity.ok(cotizacion);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{dniCliente}")
    public ResponseEntity<List<CotizacionDTO>> getCotizacionesPorCliente(@PathVariable String dniCliente) {
        try {
            List<CotizacionDTO> cotizaciones = cotizacionService.getCotizacionesByCliente(dniCliente);
            return ResponseEntity.ok(cotizaciones);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<CotizacionDTO>> searchCotizaciones(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String dniCliente,
            @RequestParam(required = false) String dniEmpleado) {
        try {
            List<CotizacionDTO> cotizaciones = cotizacionService.searchCotizaciones(
                    fechaInicio, fechaFin, estado, dniCliente, dniEmpleado);
            return ResponseEntity.ok(cotizaciones);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/estadisticas/contar")
    public ResponseEntity<Long> contarCotizacionesPorEstado(@RequestParam String estado) {
        try {
            Long count = cotizacionService.contarCotizacionesPorEstado(estado);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== ENDPOINTS DE CREACIÓN ==========

    @PostMapping
    public ResponseEntity<CotizacionDTO> createCotizacion(@RequestBody CotizacionDTO cotizacionDTO) {
        try {
            CotizacionDTO createdCotizacion = cotizacionService.createCotizacion(cotizacionDTO);
            return ResponseEntity.ok(createdCotizacion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/con-productos")
    public ResponseEntity<CotizacionDTO> createCotizacionConProductos(@RequestBody CotizacionRequestDTO requestDTO) {
        try {
            CotizacionDTO createdCotizacion = cotizacionService.createCotizacionFromRequest(requestDTO);
            return ResponseEntity.ok(createdCotizacion);
        } catch (Exception e) {
            System.out.println("Error en create con productos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== ENDPOINTS DE ACTUALIZACIÓN ==========

    @PutMapping("/{id}")
    public ResponseEntity<CotizacionDTO> updateCotizacion(@PathVariable String id, @RequestBody CotizacionDTO cotizacionDTO) {
        try {
            CotizacionDTO updatedCotizacion = cotizacionService.updateCotizacion(id, cotizacionDTO);
            return ResponseEntity.ok(updatedCotizacion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/con-productos")
    public ResponseEntity<CotizacionDTO> updateCotizacionConProductos(
            @PathVariable String id,
            @RequestBody CotizacionRequestDTO requestDTO) {
        try {
            CotizacionDTO updatedCotizacion = cotizacionService.updateCotizacionWithProducts(id, requestDTO);
            return ResponseEntity.ok(updatedCotizacion);
        } catch (Exception e) {
            System.out.println("Error en update con productos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CotizacionDTO> cambiarEstadoCotizacion(
            @PathVariable String id,
            @RequestParam String nuevoEstado) {
        try {
            CotizacionDTO cotizacionActualizada = cotizacionService.cambiarEstadoCotizacion(id, nuevoEstado);
            return ResponseEntity.ok(cotizacionActualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== ENDPOINTS DE ELIMINACIÓN ==========

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCotizacion(@PathVariable String id) {
        try {
            cotizacionService.deleteCotizacion(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== ENDPOINTS DE DIAGNÓSTICO (TEMPORALES) ==========

    @PostMapping("/debug-create")
    public ResponseEntity<Object> debugCreateCotizacion(@RequestBody Object request) {
        try {
            System.out.println("=== DEBUG CREATE COTIZACION ===");
            System.out.println("Tipo de request: " + request.getClass().getSimpleName());
            System.out.println("Request completo: " + request.toString());

            // Convertir a JSON para ver la estructura
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(request);
            System.out.println("Request como JSON: " + json);

            // Intentar convertir a DTO para ver si hay errores de mapeo
            try {
                CotizacionRequestDTO requestDTO = mapper.convertValue(request, CotizacionRequestDTO.class);
                System.out.println("DTO convertido exitosamente: " + requestDTO);
            } catch (Exception e) {
                System.out.println("Error al convertir a DTO: " + e.getMessage());
                e.printStackTrace();
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Debug recibido",
                    "tipo", request.getClass().getSimpleName(),
                    "data", request
            ));
        } catch (Exception e) {
            System.out.println("Error en debug: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/debug-update")
    public ResponseEntity<Object> debugUpdateCotizacion(@PathVariable String id, @RequestBody Object request) {
        try {
            System.out.println("=== DEBUG UPDATE COTIZACION ===");
            System.out.println("ID: " + id);
            System.out.println("Tipo de request: " + request.getClass().getSimpleName());
            System.out.println("Request completo: " + request.toString());

            // Convertir a JSON para ver la estructura
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(request);
            System.out.println("Request como JSON: " + json);

            // Intentar convertir a DTO
            try {
                CotizacionRequestDTO requestDTO = mapper.convertValue(request, CotizacionRequestDTO.class);
                System.out.println("DTO convertido exitosamente: " + requestDTO);
            } catch (Exception e) {
                System.out.println("Error al convertir a DTO: " + e.getMessage());
                e.printStackTrace();
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Debug update recibido",
                    "id", id,
                    "tipo", request.getClass().getSimpleName(),
                    "data", request
            ));
        } catch (Exception e) {
            System.out.println("Error en debug update: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}