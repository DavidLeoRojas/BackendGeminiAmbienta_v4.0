package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.entity.Direccion;
import com.gemini.gemini_ambiental.entity.CargoEspecialidad;
import com.gemini.gemini_ambiental.exception.ResourceNotFoundException;
import com.gemini.gemini_ambiental.repository.PersonaRepository;
import com.gemini.gemini_ambiental.repository.DireccionRepository;
import com.gemini.gemini_ambiental.repository.CargoEspecialidadRepository;
import com.gemini.gemini_ambiental.dto.PersonaDTO;
import com.gemini.gemini_ambiental.security.PersonaDetails; // Asegúrate de importar tu clase UserDetails personalizada
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
public class PersonaService implements UserDetailsService { // <-- Implementar UserDetailsService


    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private CargoEspecialidadRepository cargoEspecialidadRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    // Método requerido por UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Persona persona = personaRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + username));
        // Retorna una instancia de tu clase UserDetails personalizada
        return new PersonaDetails(persona);
    }

    // Tu método existente findByCorreo, necesario para el JwtRequestFilter si lo usas de otra manera
    public Optional<Persona> findByCorreo(String correo) {
        return personaRepository.findByCorreo(correo);
    }

    // --- Métodos CRUD existentes (sin cambios en la lógica principal) ---

    public PersonaDTO createPersona(PersonaDTO personaDTO) {
        Persona persona = convertToEntity(personaDTO);

        if (personaDTO.getCorreo() != null) {
            personaRepository.findByCorreo(personaDTO.getCorreo())
                    .ifPresent(existing -> {
                        throw new RuntimeException("Ya existe una persona con este correo electrónico");
                    });
        }

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

        if (personaDTO.getCorreo() != null && !personaDTO.getCorreo().equals(existingPersona.getCorreo())) {
            personaRepository.findByCorreo(personaDTO.getCorreo())
                    .ifPresent(existing -> {
                        throw new RuntimeException("Ya existe una persona con este correo electrónico");
                    });
        }

        existingPersona.setNombre(personaDTO.getNombre());
        existingPersona.setTelefono(personaDTO.getTelefono());
        existingPersona.setCorreo(personaDTO.getCorreo());
        existingPersona.setRol(personaDTO.getRol());

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

    public Persona findByEmailAndDni(String email, String dni) {
        return personaRepository.findByCorreoAndDni(email, dni)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email y dni"));
    }

    // --- Métodos de conversión (sin cambios) ---

    private Persona convertToEntity(PersonaDTO dto) {
        Persona persona = new Persona();

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

        String tipoDni = dto.getTipoDni();
        if (tipoDni == null || tipoDni.trim().isEmpty()) {
            persona.setTipoDni("CC");
        } else {
            persona.setTipoDni(tipoDni);
        }

        persona.setNombre(dto.getNombre());
        persona.setTelefono(dto.getTelefono());
        persona.setCorreo(dto.getCorreo());
        persona.setRol(dto.getRol());
        persona.setTipoPersona(tipoPersonaEnum);

        if (tipoPersonaEnum == Persona.TipoPersona.Juridica) {
            persona.setNit(dto.getNit());
            persona.setRepresentanteLegal(dto.getRepresentanteLegal());
        } else {
            persona.setNit(null);
            persona.setRepresentanteLegal(null);
        }

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
        dto.setDni(persona.getDni());
        dto.setTipoDni(persona.getTipoDni());
        dto.setNombre(persona.getNombre());
        dto.setTelefono(persona.getTelefono());
        dto.setCorreo(persona.getCorreo());
        dto.setRol(persona.getRol());
        dto.setTipoPersona(persona.getTipoPersona().toString());
        dto.setRepresentanteLegal(persona.getRepresentanteLegal());
        dto.setNit(persona.getNit());


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