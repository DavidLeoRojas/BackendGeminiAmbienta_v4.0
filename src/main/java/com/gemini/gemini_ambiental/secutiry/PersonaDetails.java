package com.gemini.gemini_ambiental.security;

import com.gemini.gemini_ambiental.entity.Persona;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class PersonaDetails implements UserDetails {

    private final Persona persona;

    public PersonaDetails(Persona persona) {
        this.persona = persona;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna el rol como una autoridad, por ejemplo, "ROLE_ADMIN" o "ROLE_USER"
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + persona.getRol()));
    }

    @Override
    public String getPassword() {
        return persona.getPassword();
    }

    @Override
    public String getUsername() {
        return persona.getCorreo(); // Usamos el correo como nombre de usuario
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Ajusta según tu lógica de negocio
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Ajusta según tu lógica de negocio
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Ajusta según tu lógica de negocio
    }

    @Override
    public boolean isEnabled() {
        return true; // Ajusta según tu lógica de negocio (por ejemplo, un campo 'activo' en Persona)
    }

    // Métodos para acceder a la entidad Persona si es necesario
    public Persona getPersona() {
        return persona;
    }
}