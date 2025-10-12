    package com.gemini.gemini_ambiental.controller;


    import com.gemini.gemini_ambiental.dto.ServicioDTO;
    import com.gemini.gemini_ambiental.service.ServicioService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/servicios")
    public class ServicioController {

        @Autowired
        private ServicioService servicioService;

        @GetMapping
        public ResponseEntity<List<ServicioDTO>> getAllServicios() {
            List<ServicioDTO> servicios = servicioService.getAllServicios();
            return ResponseEntity.ok(servicios);
        }

        @GetMapping("/search")
        public ResponseEntity<List<ServicioDTO>> searchServicios(
                @RequestParam(required = false) String fecha,
                @RequestParam(required = false) String estado,
                @RequestParam(required = false) String dniEmpleado,
                @RequestParam(required = false) String dniCliente) {
            List<ServicioDTO> servicios = servicioService.searchServicios(fecha, estado, dniEmpleado, dniCliente);
            return ResponseEntity.ok(servicios);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ServicioDTO> getServicioById(@PathVariable String id) {
            ServicioDTO servicio = servicioService.getServicioById(id);
            return ResponseEntity.ok(servicio);
        }

        @PostMapping
        public ResponseEntity<ServicioDTO> createServicio(@RequestBody ServicioDTO servicioDTO) {
            ServicioDTO createdServicio = servicioService.createServicio(servicioDTO);
            return ResponseEntity.ok(createdServicio);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ServicioDTO> updateServicio(@PathVariable String id, @RequestBody ServicioDTO servicioDTO) {
            ServicioDTO updatedServicio = servicioService.updateServicio(id, servicioDTO);
            return ResponseEntity.ok(updatedServicio);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteServicio(@PathVariable String id) {
            servicioService.deleteServicio(id);
            return ResponseEntity.noContent().build();
        }

        @PatchMapping("/{id}/estado")
        public ResponseEntity<ServicioDTO> updateEstadoServicio(@PathVariable String id, @RequestBody EstadoUpdateRequest request) {
            ServicioDTO servicio = servicioService.getServicioById(id);
            servicio.setEstado(request.getEstado());
            ServicioDTO updatedServicio = servicioService.updateServicio(id, servicio);
            return ResponseEntity.ok(updatedServicio);
        }

        // Clase interna para manejar la actualización del estado
        public static class EstadoUpdateRequest {
            private String estado;

            public String getEstado() {
                return estado;
            }

            public void setEstado(String estado) {
                this.estado = estado;
            }
        }
    }