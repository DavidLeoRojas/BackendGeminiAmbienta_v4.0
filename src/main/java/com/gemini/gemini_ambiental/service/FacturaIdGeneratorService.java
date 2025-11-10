package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturaIdGeneratorService {

    @Autowired
    private FacturaRepository facturaRepository;

    public String generarNuevoIdFactura() {
        // Buscar el último ID de factura en la base de datos
        String ultimoId = facturaRepository.findLastFacturaId();

        if (ultimoId == null) {
            return "FAC001"; // Primera factura
        }

        // Extraer el número del último ID (ej: "FAC005" -> 5)
        String numeroStr = ultimoId.replace("FAC", "");
        try {
            int numero = Integer.parseInt(numeroStr);
            int nuevoNumero = numero + 1;

            // Formatear con ceros a la izquierda (FAC001, FAC002, etc.)
            return String.format("FAC%03d", nuevoNumero);
        } catch (NumberFormatException e) {
            // Si hay algún error, empezar desde FAC001
            return "FAC001";
        }
    }
}