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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Persona persona = personaRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + username));
        return new PersonaDetails(persona);
    }

    public Optional<Persona> findByCorreo(String correo) {
        return personaRepository.findByCorreo(correo);
    }

    // Solo métodos esenciales para login
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

        // ❌ ELIMINADO: dto.setFechaCreacion(persona.getFechaCreacion());

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