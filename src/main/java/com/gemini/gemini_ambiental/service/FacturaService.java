package com.gemini.gemini_ambiental.service;


import com.gemini.gemini_ambiental.entity.Factura;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.FacturaRepository;
import com.gemini.gemini_ambiental.dto.FacturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    public FacturaDTO createFactura(FacturaDTO facturaDTO) {
        Factura factura = convertToEntity(facturaDTO);
        Factura savedFactura = facturaRepository.save(factura);
        return convertToDTO(savedFactura);
    }

    public FacturaDTO updateFactura(String id, FacturaDTO facturaDTO) {
        Factura existingFactura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));

        // Actualizar campos
        existingFactura.setFechaEmision(facturaDTO.getFechaEmision());
        existingFactura.setMontoTotal(facturaDTO.getMontoTotal());
        existingFactura.setEstado(Factura.EstadoFactura.valueOf(facturaDTO.getEstado()));
        existingFactura.setObservaciones(facturaDTO.getObservaciones());
        existingFactura.setTipoFactura(Factura.TipoFactura.valueOf(facturaDTO.getTipoFactura()));

        Factura updatedFactura = facturaRepository.save(existingFactura);
        return convertToDTO(updatedFactura);
    }

    public void deleteFactura(String id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));

        facturaRepository.delete(factura);
    }

    public FacturaDTO getFacturaById(String id) {
        Factura factura = facturaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));
        return convertToDTO(factura);
    }

    public List<FacturaDTO> getAllFacturas() {
        return facturaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FacturaDTO> searchFacturas(String fechaInicio, String fechaFin, String estado, String tipoFactura, String dniCliente) {
        LocalDate start = fechaInicio != null ? LocalDate.parse(fechaInicio) : null;
        LocalDate end = fechaFin != null ? LocalDate.parse(fechaFin) : null;

        List<Factura> facturas = facturaRepository.findAll();

        if (start != null && end != null) {
            facturas = facturas.stream()
                    .filter(f -> !f.getFechaEmision().isBefore(start) && !f.getFechaEmision().isAfter(end))
                    .collect(Collectors.toList());
        }

        if (estado != null) {
            Factura.EstadoFactura estadoEnum = Factura.EstadoFactura.valueOf(estado);
            facturas = facturas.stream()
                    .filter(f -> f.getEstado() == estadoEnum)
                    .collect(Collectors.toList());
        }

        if (tipoFactura != null) {
            Factura.TipoFactura tipoEnum = Factura.TipoFactura.valueOf(tipoFactura);
            facturas = facturas.stream()
                    .filter(f -> f.getTipoFactura() == tipoEnum)
                    .collect(Collectors.toList());
        }

        if (dniCliente != null) {
            facturas = facturas.stream()
                    .filter(f -> f.getCliente() != null && f.getCliente().getDni().equals(dniCliente))
                    .collect(Collectors.toList());
        }

        return facturas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Factura convertToEntity(FacturaDTO dto) {
        Factura factura = new Factura();
        factura.setIdFactura(dto.getIdFactura());
        factura.setFechaEmision(dto.getFechaEmision());
        factura.setMontoTotal(dto.getMontoTotal());
        factura.setEstado(Factura.EstadoFactura.valueOf(dto.getEstado()));
        factura.setObservaciones(dto.getObservaciones());
        factura.setTipoFactura(Factura.TipoFactura.valueOf(dto.getTipoFactura()));
        return factura;
    }

    private FacturaDTO convertToDTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setIdFactura(factura.getIdFactura());
        dto.setDniCliente(factura.getCliente() != null ? factura.getCliente().getDni() : null);
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setMontoTotal(factura.getMontoTotal());
        dto.setEstado(factura.getEstado().toString());
        dto.setObservaciones(factura.getObservaciones());
        dto.setTipoFactura(factura.getTipoFactura().toString());
        dto.setIdCotizacion(factura.getCotizacion() != null ? factura.getCotizacion().getIdCotizacion() : null);
        dto.setFechaCreacion(factura.getFechaCreacion());

        // Agregar datos adicionales para la UI
        if (factura.getCliente() != null) {
            dto.setNombreCliente(factura.getCliente().getNombre());
            dto.setTelefonoCliente(factura.getCliente().getTelefono());
            dto.setCorreoCliente(factura.getCliente().getCorreo());
        }

        return dto;
    }
}