// src/main/java/com/gemini/gemini_ambiental/service/TipoServicioService.java
package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.dto.TipoServicioDTO;
import com.gemini.gemini_ambiental.entity.CategoriaServicio;
import com.gemini.gemini_ambiental.entity.Servicio;
import com.gemini.gemini_ambiental.entity.TipoServicio;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.CategoriaServicioRepository;
import com.gemini.gemini_ambiental.repository.ServicioRepository;
import com.gemini.gemini_ambiental.repository.TipoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoServicioService {

    @Autowired
    private ServicioRepository servicioRepository; // <-- Inyectar

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    @Autowired
    private CategoriaServicioRepository categoriaServicioRepository;

    // Método para obtener todos los tipos de servicio
    public List<TipoServicioDTO> getAllTiposServicio() {
        return tipoServicioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método para obtener un tipo de servicio por ID
    public TipoServicioDTO getTipoServicioById(String id) {
        TipoServicio tipo = tipoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado con ID: " + id));
        return convertToDTO(tipo);
    }

    // Método para crear un nuevo tipo de servicio
    public TipoServicioDTO createTipoServicio(TipoServicioDTO dto) {
        if (dto.getIdCategoriaServicio() == null || dto.getIdCategoriaServicio().isEmpty()) {
            throw new IllegalArgumentException("El ID de la categoría de servicio es obligatorio.");
        }

        CategoriaServicio categoria = categoriaServicioRepository.findById(dto.getIdCategoriaServicio())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de servicio no encontrada con ID: " + dto.getIdCategoriaServicio()));

        TipoServicio tipoServicio = new TipoServicio();
        tipoServicio.setNombreServicio(dto.getNombreServicio());
        tipoServicio.setDescripcion(dto.getDescripcion());
        tipoServicio.setCosto(dto.getCosto());
        tipoServicio.setDuracion(dto.getDuracion());
        tipoServicio.setFrecuencia(dto.getFrecuencia());
        tipoServicio.setEstado(dto.getEstado() != null ? dto.getEstado() : "ACTIVO");
        tipoServicio.setIcono(dto.getIcono());
        tipoServicio.setCategoriaServicio(categoria);

        TipoServicio savedTipoServicio = tipoServicioRepository.save(tipoServicio);
        return convertToDTO(savedTipoServicio);
    }

    // Método para actualizar un tipo de servicio
    public TipoServicioDTO updateTipoServicio(String id, TipoServicioDTO dto) {
        TipoServicio existingTipo = tipoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado con ID: " + id));

        if (dto.getIdCategoriaServicio() != null && !dto.getIdCategoriaServicio().isEmpty()) {
            CategoriaServicio categoria = categoriaServicioRepository.findById(dto.getIdCategoriaServicio())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría de servicio no encontrada con ID: " + dto.getIdCategoriaServicio()));
            existingTipo.setCategoriaServicio(categoria);
        }

        existingTipo.setNombreServicio(dto.getNombreServicio());
        existingTipo.setDescripcion(dto.getDescripcion());
        existingTipo.setCosto(dto.getCosto());
        existingTipo.setDuracion(dto.getDuracion());
        existingTipo.setFrecuencia(dto.getFrecuencia());
        existingTipo.setEstado(dto.getEstado());
        existingTipo.setIcono(dto.getIcono());

        TipoServicio updatedTipo = tipoServicioRepository.save(existingTipo);
        return convertToDTO(updatedTipo);
    }

    // Método para eliminar un tipo de servicio
    public void deleteTipoServicio(String id) {
        // 1. Verificar si el tipo de servicio existe
        TipoServicio tipo = tipoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado con ID: " + id));

        // 2. Buscar servicios agendados que usan este tipo de servicio
        // Asumiendo que Servicio tiene un campo tipoServicio (TipoServicio)
        List<Servicio> serviciosVinculados = servicioRepository.findByTipoServicio_IdTipoServicio(id);

        // 3. Verificar si hay servicios activos (no Completados ni Cancelados)
        // Asumiendo que los estados válidos para permitir la eliminación son "Completado" y "Cancelado"
        List<Servicio> serviciosActivos = serviciosVinculados.stream()
                .filter(s -> !("Completado".equals(s.getEstado()) || "Cancelado".equals(s.getEstado())))
                .toList();

        // 4. Si hay servicios activos, lanzar una excepción
        if (!serviciosActivos.isEmpty()) {
            String detalles = serviciosActivos.stream()
                    .map(s -> "ID: " + s.getIdServicio() + ", Fecha: " + s.getFecha() + ", Hora: " + s.getHora() + ", Cliente: " + (s.getCliente() != null ? s.getCliente().getNombre() : "N/A") + ", Estado: " + s.getEstado())
                    .collect(Collectors.joining("\n - ", " - ", ""));
            throw new IllegalArgumentException("No se puede eliminar el tipo de servicio '" + tipo.getNombreServicio() + "' porque hay servicios agendados activos que lo utilizan (no están Completados o Cancelados):\n" + detalles);
        }

        // 5. Si no hay servicios activos, proceder con la eliminación
        // Si solo se permiten eliminar si todos están Completados o Cancelados, no hay problema.
        // Si hay servicios Cancelados o Completados, aún se puede eliminar el tipo de servicio.
        tipoServicioRepository.deleteById(id);
    }

    // Método auxiliar para convertir entidad a DTO
    private TipoServicioDTO convertToDTO(TipoServicio tipoServicio) {
        TipoServicioDTO dto = new TipoServicioDTO();
        dto.setIdTipoServicio(tipoServicio.getIdTipoServicio());
        dto.setNombreServicio(tipoServicio.getNombreServicio());
        dto.setDescripcion(tipoServicio.getDescripcion());
        dto.setCosto(tipoServicio.getCosto());
        dto.setDuracion(tipoServicio.getDuracion());
        dto.setFrecuencia(tipoServicio.getFrecuencia());
        dto.setEstado(tipoServicio.getEstado());
        dto.setIcono(tipoServicio.getIcono());
        dto.setIdCategoriaServicio(tipoServicio.getCategoriaServicio().getIdCategoriaServicio());
        return dto;
    }
}