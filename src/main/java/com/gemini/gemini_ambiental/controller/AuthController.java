package com.gemini.gemini_ambiental.controller;

import com.gemini.gemini_ambiental.config.JwtUtil;
import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        Persona persona = personaService.findByEmailAndDni(authRequest.getEmail(), authRequest.getDni());

        if (persona == null) {
            return ResponseEntity.badRequest().body("Credenciales inválidas");
        }

        // ✅ VERIFICAR ROL: Solo permitir login si es 'Empleado'
        if (!"Empleado".equalsIgnoreCase(persona.getRol())) {
            return ResponseEntity.status(403).body("Acceso denegado. Solo empleados pueden iniciar sesión.");
        }

        final UserDetails userDetails = personaService.loadUserByUsername(persona.getCorreo());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(jwt, persona.getDni(), persona.getNombre()));
    }
}

class AuthRequest {
    private String email;
    private String dni;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
}

class AuthResponse {
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