package com.gemini.gemini_ambiental.controller;

import com.gemini.gemini_ambiental.config.JwtUtil;
import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {

        Persona persona = personaService.findByCorreo(authRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Correo no registrado"));

        if (!persona.getRol().equalsIgnoreCase("EMPLEADO")) {
            return ResponseEntity.status(403).body("Acceso permitido solo para empleados");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getDni()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("DNI incorrecto");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        // *** IMPORTANTE: agregar 'Bearer ' al token ***
        return ResponseEntity.ok(new AuthResponse("Bearer " + jwt, persona.getDni(), persona.getNombre()));
    }

    static class AuthRequest {
        private String email;
        private String dni;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getDni() { return dni; }
        public void setDni(String dni) { this.dni = dni; }
    }

    static class AuthResponse {
        private String token;
        private String id;
        private String nombre;

        public AuthResponse(String token, String id, String nombre) {
            this.token = token;
            this.id = id;
            this.nombre = nombre;
        }

        public String getToken() { return token; }
        public String getId() { return id; }
        public String getNombre() { return nombre; }
    }
}

