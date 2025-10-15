package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.entity.Direccion;
import com.gemini.gemini_ambiental.entity.CargoEspecialidad;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.PersonaRepository;
import com.gemini.gemini_ambiental.repository.DireccionRepository;
import com.gemini.gemini_ambiental.repository.CargoEspecialidadRepository;
import com.gemini.gemini_ambiental.dto.PersonaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy; // Importar Lazy
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonaService implements UserDetailsService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private CargoEspecialidadRepository cargoEspecialidadRepository;

    // ✅ Inyectar PasswordEncoder con @Lazy para romper la dependencia circular en tiempo de creación
    @Autowired
    @Lazy // <-- Añadido @Lazy aquí
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Persona persona = personaRepository.findByCorreo(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + email));

        return User.builder()
                .username(persona.getCorreo())
                .password(persona.getPassword()) // La contraseña debe estar hasheada
                .authorities("USER") // Puedes personalizar los roles aquí si es necesario
                .build();
    }

    public PersonaDTO createPersona(PersonaDTO personaDTO) {
        Persona persona = convertToEntity(personaDTO);

        // Validar si el correo ya existe
        if (personaDTO.getCorreo() != null) {
            personaRepository.findByCorreo(personaDTO.getCorreo())
                    .ifPresent(existing -> {
                        throw new RuntimeException("Ya existe una persona con este correo electrónico");
                    });
        }

        // ✅ GENERAR CONTRASEÑA AUTOMÁTICAMENTE ANTES DE GUARDAR
        String dni = persona.getDni();
        String correo = persona.getCorreo();
        if (dni == null || dni.trim().isEmpty() || correo == null || correo.trim().isEmpty()) {
            throw new RuntimeException("El DNI y el correo son obligatorios para generar la contraseña.");
        }
        String contrasenaPlana = dni + correo.toLowerCase();
        // ✅ Hashear la contraseña antes de guardarla usando el bean inyectado perezosamente
        persona.setPassword(passwordEncoder.encode(contrasenaPlana));

        Persona savedPersona = personaRepository.save(persona);
        return convertToDTO(savedPersona);
    }

    public PersonaDTO updatePersona(String dni, PersonaDTO personaDTO) {
        Persona existingPersona = personaRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con DNI: " + dni));

        // Validar si el correo ya existe (excluyendo el actual)
        if (personaDTO.getCorreo() != null && !personaDTO.getCorreo().equals(existingPersona.getCorreo())) {
            personaRepository.findByCorreo(personaDTO.getCorreo())
                    .ifPresent(existing -> {
                        throw new RuntimeException("Ya existe una persona con este correo electrónico");
                    });
        }

        // Actualizar campos
        existingPersona.setNombre(personaDTO.getNombre());
        existingPersona.setTelefono(personaDTO.getTelefono());
        existingPersona.setCorreo(personaDTO.getCorreo());
        existingPersona.setRol(personaDTO.getRol());
        // Actualizar tipoPersona con manejo de null
        String tipoPersonaStr = personaDTO.getTipoPersona();
        if (tipoPersonaStr == null || tipoPersonaStr.trim().isEmpty()) {
            existingPersona.setTipoPersona(Persona.TipoPersona.Natural);
        } else {
            try {
                existingPersona.setTipoPersona(Persona.TipoPersona.valueOf(tipoPersonaStr));
            } catch (IllegalArgumentException e) {
                existingPersona.setTipoPersona(Persona.TipoPersona.Natural);
            }
        }
        existingPersona.setRepresentanteLegal(personaDTO.getRepresentanteLegal());
        existingPersona.setNit(personaDTO.getNit());

        // Actualizar relaciones
        if (personaDTO.getIdDireccion() != null) {
            Direccion direccion = direccionRepository.findById(personaDTO.getIdDireccion())
                    .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada con ID: " + personaDTO.getIdDireccion()));
            existingPersona.setDireccion(direccion);
        } else {
            existingPersona.setDireccion(null);
        }

        if (personaDTO.getIdCargoEspecialidad() != null) {
            CargoEspecialidad cargo = cargoEspecialidadRepository.findById(personaDTO.getIdCargoEspecialidad())
                    .orElseThrow(() -> new ResourceNotFoundException("Cargo no encontrado con ID: " + personaDTO.getIdCargoEspecialidad()));
            existingPersona.setCargoEspecialidad(cargo);
        } else {
            existingPersona.setCargoEspecialidad(null);
        }

        Persona updatedPersona = personaRepository.save(existingPersona);
        return convertToDTO(updatedPersona);
    }

    public void deletePersona(String dni) {
        Persona persona = personaRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con DNI: " + dni));

        // Verificar si tiene relaciones críticas antes de eliminar
        // (Implementar lógica de verificación según sea necesario)
        // Ejemplo: verificar cotizaciones, servicios, facturas asociadas

        personaRepository.delete(persona);
    }

    public PersonaDTO getPersonaByDni(String dni) {
        Persona persona = personaRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con DNI: " + dni));
        return convertToDTO(persona);
    }

    public List<PersonaDTO> getAllPersonas() {
        return personaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PersonaDTO> searchPersonas(String searchTerm) {
        // Implementar búsqueda más compleja si es necesario
        return personaRepository.findAll().stream()
                .filter(p -> searchTerm == null || searchTerm.isEmpty() ||
                        p.getNombre().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        p.getDni().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        (p.getCorreo() != null && p.getCorreo().toLowerCase().contains(searchTerm.toLowerCase())))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Persona findByEmailAndDni(String email, String dni) {
        return personaRepository.findByCorreoAndDni(email, dni)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email y dni"));
    }

    private Persona convertToEntity(PersonaDTO dto) {
        Persona persona = new Persona();
        persona.setDni(dto.getDni());

        // --- CORRECCIÓN: Manejar tipoDni nulo o vacío ---
        String tipoDni = dto.getTipoDni();
        if (tipoDni == null || tipoDni.trim().isEmpty()) {
            persona.setTipoDni("CC");
        } else {
            persona.setTipoDni(tipoDni);
        }
        // --- FIN CORRECCIÓN ---

        persona.setNombre(dto.getNombre());
        persona.setTelefono(dto.getTelefono());
        persona.setCorreo(dto.getCorreo());
        persona.setRol(dto.getRol());

        // Asegúrate de manejar tipoPersona de la misma manera si aplica
        String tipoPersonaStr = dto.getTipoPersona();
        if (tipoPersonaStr == null || tipoPersonaStr.trim().isEmpty()) {
            persona.setTipoPersona(Persona.TipoPersona.Natural);
        } else {
            try {
                persona.setTipoPersona(Persona.TipoPersona.valueOf(tipoPersonaStr));
            } catch (IllegalArgumentException e) {
                persona.setTipoPersona(Persona.TipoPersona.Natural);
            }
        }

        persona.setRepresentanteLegal(dto.getRepresentanteLegal());
        persona.setNit(dto.getNit());

        if (dto.getIdDireccion() != null) {
            Direccion direccion = direccionRepository.findById(dto.getIdDireccion())
                    .orElse(null);
            persona.setDireccion(direccion);
        }
        if (dto.getIdCargoEspecialidad() != null) {
            CargoEspecialidad cargo = cargoEspecialidadRepository.findById(dto.getIdCargoEspecialidad())
                    .orElse(null);
            persona.setCargoEspecialidad(cargo);
        }
        return persona;
    }

    private PersonaDTO convertToDTO(Persona persona) {
        PersonaDTO dto = new PersonaDTO();
        dto.setDni(persona.getDni());
        dto.setTipoDni(persona.getTipoDni());
        dto.setNombre(persona.getNombre());
        dto.setTelefono(persona.getTelefono());
        dto.setCorreo(persona.getCorreo());
        dto.setRol(persona.getRol());
        dto.setTipoPersona(persona.getTipoPersona().toString());
        dto.setRepresentanteLegal(persona.getRepresentanteLegal());
        dto.setNit(persona.getNit());
        dto.setFechaCreacion(persona.getFechaCreacion());

        // Mapear relaciones para la UI
        if (persona.getDireccion() != null) {
            dto.setIdDireccion(persona.getDireccion().getIdDireccion());
            dto.setNombreDireccion(persona.getDireccion().getNombre());
        }
        if (persona.getCargoEspecialidad() != null) {
            dto.setIdCargoEspecialidad(persona.getCargoEspecialidad().getIdCargoEspecialidad());
            dto.setNombreCargo(persona.getCargoEspecialidad().getNombre());
        }

        return dto;
    }
}