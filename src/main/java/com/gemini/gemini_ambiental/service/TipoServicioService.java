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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TipoServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private TipoServicioRepository tipoServicioRepository; // Asegúrate que este repo tenga el método findAll()

    @Autowired
    private CategoriaServicioRepository categoriaServicioRepository;

    public List<TipoServicioDTO> getAllTiposServicio() {
        return tipoServicioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TipoServicioDTO getTipoServicioById(String id) {
        TipoServicio tipo = tipoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado con ID: " + id));
        return convertToDTO(tipo);
    }

    public TipoServicioDTO createTipoServicio(TipoServicioDTO dto) {
        if (dto.getIdCategoriaServicio() == null || dto.getIdCategoriaServicio().isEmpty()) {
            throw new IllegalArgumentException("El ID de la categoría de servicio es obligatorio.");
        }

        CategoriaServicio categoria = categoriaServicioRepository.findById(dto.getIdCategoriaServicio())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de servicio no encontrada con ID: " + dto.getIdCategoriaServicio()));

        // --- PASO 2: Generar el ID aquí ---
        String nuevoId = generateNextId();
        // ----------------------------------

        TipoServicio tipoServicio = TipoServicio.builder()
                .idTipoServicio(nuevoId) // <-- Asignar el ID generado aquí
                .nombreServicio(dto.getNombreServicio())
                .descripcion(dto.getDescripcion())
                .costo(dto.getCosto())
                .duracion(dto.getDuracion())
                .frecuencia(dto.getFrecuencia())
                .estado(dto.getEstado() != null ? dto.getEstado() : "ACTIVO")
                .icono(dto.getIcono())
                .categoriaServicio(categoria)
                .build();

        TipoServicio savedTipoServicio = tipoServicioRepository.save(tipoServicio);
        return convertToDTO(savedTipoServicio);
    }

    // --- MÉTODO AUXILIAR PARA GENERAR EL SIGUIENTE ID ---
    private String generateNextId() {
        // 1. Obtener todos los IDs existentes
        List<TipoServicio> todosLosTipos = tipoServicioRepository.findAll();
        List<String> idsExistentes = todosLosTipos.stream()
                .map(TipoServicio::getIdTipoServicio)
                .filter(id -> id != null && id.startsWith("TPS")) // Filtrar solo los que empiezan con "TPS"
                .collect(Collectors.toList());

        // 2. Si no hay ninguno, devolver el primero
        if (idsExistentes.isEmpty()) {
            return "TPS001";
        }

        // 3. Encontrar el número más alto
        int maxNumero = idsExistentes.stream()
                .mapToInt(this::extractNumber) // Extraer el número de cada ID
                .max()
                .orElse(0); // Si no hay coincidencias válidas, usar 0

        // 4. Calcular el siguiente número
        int siguienteNumero = maxNumero + 1;

        // 5. Formatear y devolver el nuevo ID
        return String.format("TPS%03d", siguienteNumero);
    }

    // --- MÉTODO AUXILIAR PARA EXTRAER EL NÚMERO ---
    private int extractNumber(String id) {
        // Usar expresiones regulares para extraer el número de 3 dígitos al final
        Pattern pattern = Pattern.compile("^TPS(\\d{3})$");
        Matcher matcher = pattern.matcher(id);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            // Si el formato no coincide, devolver 0 para no afectar el cálculo del máximo
            // Considera loggear este caso si es inesperado
            return 0;
        }
    }
    // --- FIN MÉTODOS AUXILIARES ---

    public TipoServicioDTO updateTipoServicio(String id, TipoServicioDTO dto) {
        TipoServicio existingTipo = tipoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado con ID: " + id));

        if (dto.getIdCategoriaServicio() != null && !dto.getIdCategoriaServicio().isEmpty()) {
            CategoriaServicio categoria = categoriaServicioRepository.findById(dto.getIdCategoriaServicio())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría de servicio no encontrada con ID: " + dto.getIdCategoriaServicio()));
            existingTipo.setCategoriaServicio(categoria);
        }

        if (dto.getNombreServicio() != null) {
            existingTipo.setNombreServicio(dto.getNombreServicio());
        }
        if (dto.getDescripcion() != null) {
            existingTipo.setDescripcion(dto.getDescripcion());
        }
        if (dto.getCosto() != null) {
            existingTipo.setCosto(dto.getCosto());
        }
        if (dto.getDuracion() != null) {
            existingTipo.setDuracion(dto.getDuracion());
        }
        if (dto.getFrecuencia() != null) {
            existingTipo.setFrecuencia(dto.getFrecuencia());
        }
        if (dto.getEstado() != null) {
            existingTipo.setEstado(dto.getEstado());
        }
        if (dto.getIcono() != null) {
            existingTipo.setIcono(dto.getIcono());
        }

        TipoServicio updatedTipo = tipoServicioRepository.save(existingTipo);
        return convertToDTO(updatedTipo);
    }

    public void deleteTipoServicio(String id) {
        TipoServicio tipo = tipoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado con ID: " + id));

        // Verificar si hay servicios vinculados (si el método existe en el repository)
        try {
            List<Servicio> serviciosVinculados = servicioRepository.findByTipoServicio_IdTipoServicio(id);

            if (!serviciosVinculados.isEmpty()) {
                List<Servicio> serviciosActivos = serviciosVinculados.stream()
                        .filter(s -> !("Completado".equals(s.getEstado()) || "Cancelado".equals(s.getEstado())))
                        .collect(Collectors.toList());

                if (!serviciosActivos.isEmpty()) {
                    String detalles = serviciosActivos.stream()
                            .map(s -> "ID: " + s.getIdServicio() + ", Estado: " + s.getEstado())
                            .collect(Collectors.joining("\n - ", " - ", ""));
                    throw new IllegalArgumentException("No se puede eliminar el tipo de servicio '" + tipo.getNombreServicio() + "' porque hay servicios activos:\n" + detalles);
                }
            }
        } catch (Exception e) {
            // Si el método no existe, continuar con la eliminación
            System.out.println("Advertencia: No se pudo verificar servicios vinculados: " + e.getMessage());
        }

        tipoServicioRepository.deleteById(id);
    }

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

        if (tipoServicio.getCategoriaServicio() != null) {
            dto.setIdCategoriaServicio(tipoServicio.getCategoriaServicio().getIdCategoriaServicio());
        }

        return dto;
    }

    // Método para obtener tipos de servicio por categoría
    public List<TipoServicioDTO> getTiposServicioByCategoria(String idCategoria) {
        return tipoServicioRepository.findAll().stream()
                .filter(tipo -> tipo.getCategoriaServicio() != null &&
                        tipo.getCategoriaServicio().getIdCategoriaServicio().equals(idCategoria))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método para obtener tipos de servicio activos
    public List<TipoServicioDTO> getTiposServicioActivos() {
        return tipoServicioRepository.findAll().stream()
                .filter(tipo -> "ACTIVO".equals(tipo.getEstado()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}