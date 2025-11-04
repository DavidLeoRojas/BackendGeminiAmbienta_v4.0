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
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Persona persona = personaRepository.findByCorreo(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + email));

        // ✅ VALIDACIÓN CLAVE: Solo permitir login a empleados
        if (!"Empleado".equals(persona.getRol())) {
            throw new UsernameNotFoundException("Acceso denegado: Solo los empleados pueden iniciar sesión.");
        }

        return User.builder()
                .username(persona.getCorreo())
                .password(persona.getPassword())
                .authorities("ROLE_Empleado")
                .build();
    }

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

        // --- Corrección: Convertir String → UUID para dirección ---
        if (personaDTO.getIdDireccion() != null) {
            try {
                UUID idDireccion = UUID.fromString(personaDTO.getIdDireccion());
                Direccion direccion = direccionRepository.findById(String.valueOf(idDireccion))
                        .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada con ID: " + personaDTO.getIdDireccion()));
                existingPersona.setDireccion(direccion);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("ID de dirección inválido: debe ser un UUID válido");
            }
        } else {
            existingPersona.setDireccion(null);
        }

        // --- Corrección: Convertir String → UUID para cargo ---
        if (personaDTO.getIdCargoEspecialidad() != null) {
            try {
                UUID idCargo = UUID.fromString(personaDTO.getIdCargoEspecialidad());
                CargoEspecialidad cargo = cargoEspecialidadRepository.findById(idCargo)
                        .orElseThrow(() -> new ResourceNotFoundException("Cargo no encontrado con ID: " + personaDTO.getIdCargoEspecialidad()));
                existingPersona.setCargoEspecialidad(cargo);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("ID de cargo inválido: debe ser un UUID válido");
            }
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

        // --- Corrección en convertToEntity también ---
        if (dto.getIdDireccion() != null) {
            try {
                UUID idDireccion = UUID.fromString(dto.getIdDireccion());
                Direccion direccion = direccionRepository.findById(String.valueOf(idDireccion)).orElse(null);
                persona.setDireccion(direccion);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("ID de dirección inválido en DTO: debe ser un UUID válido");
            }
        }

        if (dto.getIdCargoEspecialidad() != null) {
            try {
                UUID idCargo = UUID.fromString(dto.getIdCargoEspecialidad());
                CargoEspecialidad cargo = cargoEspecialidadRepository.findById(idCargo).orElse(null);
                persona.setCargoEspecialidad(cargo);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("ID de cargo inválido en DTO: debe ser un UUID válido");
            }
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

        // ✅ CORRECCIÓN PRINCIPAL: Convertir UUID → String
        if (persona.getDireccion() != null) {
            dto.setIdDireccion(persona.getDireccion().getIdDireccion().toString());
            dto.setNombreDireccion(persona.getDireccion().getNombre());
        }
        if (persona.getCargoEspecialidad() != null) {
            dto.setIdCargoEspecialidad(persona.getCargoEspecialidad().getIdCargoEspecialidad().toString());
            dto.setNombreCargo(persona.getCargoEspecialidad().getNombre());
        }

        return dto;
    }
}