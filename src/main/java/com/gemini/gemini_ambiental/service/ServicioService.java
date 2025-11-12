package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.dto.ServicioDTO;
import com.gemini.gemini_ambiental.entity.Cotizacion;
import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.entity.Servicio;
import com.gemini.gemini_ambiental.entity.TipoServicio;
import com.gemini.gemini_ambiental.repository.CotizacionRepository;
import com.gemini.gemini_ambiental.repository.PersonaRepository;
import com.gemini.gemini_ambiental.repository.ServicioRepository;
import com.gemini.gemini_ambiental.repository.TipoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    public List<ServicioDTO> getAllServicios() {
        List<Servicio> servicios = servicioRepository.findAll();
        return servicios.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ServicioDTO> searchServicios(String fecha, String estado, String dniEmpleado, String dniCliente) {
        // Implementa la lógica de búsqueda según tus necesidades
        // Por ahora, devuelve todos
        return getAllServicios();
    }

    public ServicioDTO getServicioById(String id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
        return convertToDTO(servicio);
    }

    // --- MÉTODO CORREGIDO: createServicio ---
    public ServicioDTO createServicio(ServicioDTO servicioDTO) {
        // 1. Generar el ID antes de construir el objeto Servicio
        String nuevoId = generateNextId();

        Servicio servicio = new Servicio();
        servicio.setIdServicio(nuevoId); // <-- ASIGNAR EL ID GENERADO AQUÍ

        servicio.setFecha(servicioDTO.getFecha());
        servicio.setHora(servicioDTO.getHora());
        servicio.setEstado(mapStringToEstado(servicioDTO.getEstado()));
        servicio.setObservaciones(servicioDTO.getObservaciones());
        servicio.setPrioridad(servicioDTO.getPrioridad());
        servicio.setDuracionEstimada(servicioDTO.getDuracionEstimada());
        servicio.setServicioSinCotizacion(servicioDTO.getServicioSinCotizacion());

        if (servicioDTO.getIdCotizacion() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(servicioDTO.getIdCotizacion())
                    .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + servicioDTO.getIdCotizacion()));
            servicio.setCotizacion(cotizacion);
        }

        if (servicioDTO.getDniCliente() != null) {
            Persona cliente = personaRepository.findByDni(servicioDTO.getDniCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con DNI: " + servicioDTO.getDniCliente()));
            servicio.setCliente(cliente);
        }

        if (servicioDTO.getDniEmpleadoAsignado() != null) {
            Persona tecnico = personaRepository.findByDni(servicioDTO.getDniEmpleadoAsignado())
                    .orElseThrow(() -> new RuntimeException("Técnico no encontrado con DNI: " + servicioDTO.getDniEmpleadoAsignado()));
            servicio.setEmpleadoAsignado(tecnico);
        }

        if (servicioDTO.getIdTipoServicio() != null) {
            TipoServicio tipoServ = tipoServicioRepository.findById(servicioDTO.getIdTipoServicio())
                    .orElseThrow(() -> new RuntimeException("Tipo de Servicio no encontrado con ID: " + servicioDTO.getIdTipoServicio()));
            servicio.setTipoServicio(tipoServ);
        }

        Servicio savedServicio = servicioRepository.save(servicio);
        return convertToDTO(savedServicio);
    }
    // --- FIN MÉTODO CORREGIDO ---

    // --- MÉTODO AUXILIAR PARA GENERAR EL SIGUIENTE ID ---
    private String generateNextId() {
        // 1. Obtener todos los IDs existentes
        List<Servicio> todosLosServicios = servicioRepository.findAll();
        List<String> idsExistentes = todosLosServicios.stream()
                .map(Servicio::getIdServicio)
                .filter(id -> id != null && id.startsWith("SER")) // Filtrar solo los que empiezan con "SER"
                .collect(Collectors.toList());

        // 2. Si no hay ninguno, devolver el primero
        if (idsExistentes.isEmpty()) {
            return "SER001";
        }

        // 3. Encontrar el número más alto
        int maxNumero = idsExistentes.stream()
                .mapToInt(this::extractNumber) // Extraer el número de cada ID
                .max()
                .orElse(0); // Si no hay coincidencias válidas, usar 0

        // 4. Calcular el siguiente número
        int siguienteNumero = maxNumero + 1;

        // 5. Formatear y devolver el nuevo ID
        return String.format("SER%03d", siguienteNumero);
    }

    // --- MÉTODO AUXILIAR PARA EXTRAER EL NÚMERO ---
    private int extractNumber(String id) {
        // Usar expresiones regulares para extraer el número de 3 dígitos al final
        Pattern pattern = Pattern.compile("^SER(\\d{3})$");
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

    public ServicioDTO updateServicio(String id, ServicioDTO servicioDTO) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));

        servicio.setFecha(servicioDTO.getFecha());
        servicio.setHora(servicioDTO.getHora());
        servicio.setEstado(mapStringToEstado(servicioDTO.getEstado()));
        servicio.setObservaciones(servicioDTO.getObservaciones());
        servicio.setPrioridad(servicioDTO.getPrioridad());
        servicio.setDuracionEstimada(servicioDTO.getDuracionEstimada());

        if (servicioDTO.getIdCotizacion() != null) {
            Cotizacion cotizacion = cotizacionRepository.findById(servicioDTO.getIdCotizacion())
                    .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + servicioDTO.getIdCotizacion()));
            servicio.setCotizacion(cotizacion);
        } else {
            servicio.setCotizacion(null);
        }

        if (servicioDTO.getDniCliente() != null) {
            Persona cliente = personaRepository.findByDni(servicioDTO.getDniCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con DNI: " + servicioDTO.getDniCliente()));
            servicio.setCliente(cliente);
        } else {
            servicio.setCliente(null);
        }

        if (servicioDTO.getDniEmpleadoAsignado() != null) {
            Persona tecnico = personaRepository.findByDni(servicioDTO.getDniEmpleadoAsignado())
                    .orElseThrow(() -> new RuntimeException("Técnico no encontrado con DNI: " + servicioDTO.getDniEmpleadoAsignado()));
            servicio.setEmpleadoAsignado(tecnico);
        } else {
            servicio.setEmpleadoAsignado(null);
        }

        if (servicioDTO.getIdTipoServicio() != null) {
            TipoServicio tipoServ = tipoServicioRepository.findById(servicioDTO.getIdTipoServicio())
                    .orElseThrow(() -> new RuntimeException("Tipo de Servicio no encontrado con ID: " + servicioDTO.getIdTipoServicio()));
            servicio.setTipoServicio(tipoServ);
        } else {
            servicio.setTipoServicio(null);
        }

        Servicio updatedServicio = servicioRepository.save(servicio);
        return convertToDTO(updatedServicio);
    }

    public void deleteServicio(String id) {
        if (!servicioRepository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado con ID: " + id);
        }
        servicioRepository.deleteById(id);
    }

    /**
     * Convierte un String (ej: "PROGRAMADO") al enum correspondiente.
     * Es tolerante a mayúsculas/minúsculas y nulos.
     */
    private Servicio.EstadoServicio mapStringToEstado(String estadoStr) {
        if (estadoStr == null || estadoStr.trim().isEmpty()) {
            return Servicio.EstadoServicio.PROGRAMADO;
        }
        try {
            return Servicio.EstadoServicio.valueOf(estadoStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Estado de servicio desconocido: '" + estadoStr + "'. Usando PROGRAMADO por defecto.");
            return Servicio.EstadoServicio.PROGRAMADO;
        }
    }

    private ServicioDTO convertToDTO(Servicio servicio) {
        ServicioDTO dto = new ServicioDTO();
        dto.setIdServicio(servicio.getIdServicio());
        dto.setIdCotizacion(servicio.getCotizacion() != null ? servicio.getCotizacion().getIdCotizacion() : null);
        dto.setDniCliente(servicio.getCliente() != null ? servicio.getCliente().getDni() : null);
        dto.setDniEmpleadoAsignado(servicio.getEmpleadoAsignado() != null ? servicio.getEmpleadoAsignado().getDni() : null);
        dto.setIdTipoServicio(servicio.getTipoServicio() != null ? servicio.getTipoServicio().getIdTipoServicio() : null);
        dto.setFecha(servicio.getFecha());
        dto.setHora(servicio.getHora());
        dto.setEstado(servicio.getEstado().name()); // ✅ Usa .name() para devolver "PROGRAMADO", no "Programado"
        dto.setObservaciones(servicio.getObservaciones());
        dto.setPrioridad(servicio.getPrioridad());
        dto.setDuracionEstimada(servicio.getDuracionEstimada());
        dto.setServicioSinCotizacion(servicio.getServicioSinCotizacion());
        return dto;
    }
}