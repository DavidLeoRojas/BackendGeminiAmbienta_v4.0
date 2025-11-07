package com.gemini.gemini_ambiental.service;

import com.gemini.gemini_ambiental.entity.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PersonaService personaService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Persona persona = personaService.findByCorreo(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        if (!"Empleado".equalsIgnoreCase(persona.getRol())) {
            throw new UsernameNotFoundException("Acceso denegado: Solo empleados pueden iniciar sesión.");
        }

        // Login actual: correo + DNI como clave
        String rawPassword = persona.getDni();

        return User.builder()
                .username(persona.getCorreo())
                .password(rawPassword)
                .roles("Empleado")
                .build();
    }

}
