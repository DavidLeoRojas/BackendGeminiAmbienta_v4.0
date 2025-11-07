package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.entity.Direccion;
import com.gemini.gemini_ambiental.entity.CargoEspecialidad;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.PersonaRepository;
import com.gemini.gemini_ambiental.repository.DireccionRepository;
import com.gemini.gemini_ambiental.repository.CargoEspecialidadRepository;
import com.gemini.gemini_ambiental.dto.PersonaDTO;
import com.gemini.gemini_ambiental.security.PersonaDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    // ========== MÉTODOS DE SPRING SECURITY ==========

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Persona persona = personaRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + username));
        return new PersonaDetails(persona);
    }

    public Optional<Persona> findByCorreo(String correo) {
        return personaRepository.findByCorreo(correo);
    }

    public Persona findByEmailAndDni(String email, String dni) {
        return personaRepository.findByCorreoAndDni(email, dni)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email y dni"));
    }

    // ========== MÉTODOS CRUD COMPLETOS ==========

    public PersonaDTO createPersona(PersonaDTO personaDTO) {
        Persona persona = convertToEntity(personaDTO);

        // Validar correo único
        if (personaDTO.getCorreo() != null) {
            personaRepository.findByCorreo(personaDTO.getCorreo())
                    .ifPresent(existing -> {
                        throw new RuntimeException("Ya existe una persona con este correo electrónico");
                    });
        }

        // Generar contraseña automática
        String dni = persona.getDni();
        String correo = persona.getCorreo();
        if (dni == null || dni.trim().isEmpty() || correo == null || correo.trim().isEmpty()) {
            throw new RuntimeException("El DNI y el correo son obligatorios para generar la contraseña.");
        }
        String contrasenaPlana = dni + correo.toLowerCase();
        persona.setPassword(passwordEncoder.encode(contrasenaPlana));

        Persona savedPersona = personaRepository.save(persona);
        return convertToDTO(savedPersona);
    }

    public PersonaDTO updatePersona(String dni, PersonaDTO personaDTO) {
        Persona existingPersona = personaRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con DNI: " + dni));

        // Validar correo único si cambió
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

        // Tipo de persona
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

        // Dirección
        if (personaDTO.getIdDireccion() != null) {
            Direccion direccion = direccionRepository.findById(personaDTO.getIdDireccion())
                    .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada con ID: " + personaDTO.getIdDireccion()));
            existingPersona.setDireccion(direccion);
        } else {
            existingPersona.setDireccion(null);
        }

        // Cargo/Especialidad
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
        return personaRepository.findAll().stream()
                .filter(p -> searchTerm == null || searchTerm.isEmpty() ||
                        p.getNombre().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        p.getDni().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        (p.getCorreo() != null && p.getCorreo().toLowerCase().contains(searchTerm.toLowerCase())))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTODOS DE CONVERSIÓN ==========

    private Persona convertToEntity(PersonaDTO dto) {
        Persona persona = new Persona();

        // Tipo de persona
        String tipoPersonaStr = dto.getTipoPersona();
        Persona.TipoPersona tipoPersonaEnum;
        if (tipoPersonaStr == null || tipoPersonaStr.trim().isEmpty()) {
            tipoPersonaEnum = Persona.TipoPersona.Natural;
        } else {
            try {
                tipoPersonaEnum = Persona.TipoPersona.valueOf(tipoPersonaStr);
            } catch (IllegalArgumentException e) {
                tipoPersonaEnum = Persona.TipoPersona.Natural;
            }
        }

        // DNI/NIT según tipo de persona
        if (tipoPersonaEnum == Persona.TipoPersona.Juridica) {
            String nit = dto.getNit();
            if (nit != null && !nit.trim().isEmpty()) {
                persona.setDni(nit.trim());
            } else {
                persona.setDni(null);
            }
        } else {
            persona.setDni(dto.getDni());
        }

        // Tipo de documento
        String tipoDni = dto.getTipoDni();
        if (tipoDni == null || tipoDni.trim().isEmpty()) {
            persona.setTipoDni("CC");
        } else {
            persona.setTipoDni(tipoDni);
        }

        // Campos básicos
        persona.setNombre(dto.getNombre());
        persona.setTelefono(dto.getTelefono());
        persona.setCorreo(dto.getCorreo());
        persona.setRol(dto.getRol());
        persona.setTipoPersona(tipoPersonaEnum);

        // Campos específicos por tipo
        if (tipoPersonaEnum == Persona.TipoPersona.Juridica) {
            persona.setNit(dto.getNit());
            persona.setRepresentanteLegal(dto.getRepresentanteLegal());
        } else {
            persona.setNit(null);
            persona.setRepresentanteLegal(null);
        }

        // Relaciones
        if (dto.getIdDireccion() != null) {
            Direccion direccion = direccionRepository.findById(dto.getIdDireccion()).orElse(null);
            persona.setDireccion(direccion);
        }

        if (dto.getIdCargoEspecialidad() != null) {
            CargoEspecialidad cargo = cargoEspecialidadRepository.findById(dto.getIdCargoEspecialidad()).orElse(null);
            persona.setCargoEspecialidad(cargo);
        }

        return persona;
    }

    private PersonaDTO convertToDTO(Persona persona) {
        PersonaDTO dto = new PersonaDTO();

        // Campos básicos
        dto.setDni(persona.getDni());
        dto.setTipoDni(persona.getTipoDni());
        dto.setNombre(persona.getNombre());
        dto.setTelefono(persona.getTelefono());
        dto.setCorreo(persona.getCorreo());
        dto.setRol(persona.getRol());
        dto.setTipoPersona(persona.getTipoPersona().toString());
        dto.setRepresentanteLegal(persona.getRepresentanteLegal());
        dto.setNit(persona.getNit());

        // ❌ SOLO ESTA LÍNEA FUE ELIMINADA - fechaCreacion causa el error
        // dto.setFechaCreacion(persona.getFechaCreacion());

        // Dirección
        if (persona.getDireccion() != null) {
            dto.setIdDireccion(persona.getDireccion().getIdDireccion());
            dto.setNombreDireccion(persona.getDireccion().getNombre());
        }

        // Cargo/Especialidad
        if (persona.getCargoEspecialidad() != null) {
            dto.setIdCargoEspecialidad(persona.getCargoEspecialidad().getIdCargoEspecialidad());
            dto.setNombreCargo(persona.getCargoEspecialidad().getNombre());
        }

        return dto;
    }

    // ========== MÉTODOS ADICIONALES ==========

    public List<PersonaDTO> getPersonasByRol(String rol) {
        return personaRepository.findByRol(rol).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PersonaDTO> getPersonasByTipo(String tipoPersona) {
        return personaRepository.findByTipoPersona(tipoPersona).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean existsByDni(String dni) {
        return personaRepository.findByDni(dni).isPresent();
    }

    public boolean existsByCorreo(String correo) {
        return personaRepository.findByCorreo(correo).isPresent();
    }

    public Long countClientes() {
        return personaRepository.countClientes();
    }

    public Long countEmpleados() {
        return personaRepository.countEmpleados();
    }
}