package com.gemini.gemini_ambiental.controller;

import com.gemini.gemini_ambiental.config.JwtUtil;
import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PersonaRepository personaRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            System.out.println("Login attempt received: " + loginRequest);

            String email = loginRequest.get("email");
            String dni = loginRequest.get("dni");

            System.out.println("Email: " + email + ", DNI: " + dni);

            // Validaciones básicas
            if (email == null || email.trim().isEmpty() || dni == null || dni.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Email y DNI son requeridos"
                ));
            }

            // Buscar usuario por email y DNI
            Optional<Persona> personaOpt = personaRepository.findByCorreoAndDni(email, dni);

            if (!personaOpt.isPresent()) {
                System.out.println("Credenciales inválidas para: " + email);
                return ResponseEntity.status(401).body(Map.of(
                        "error", "Credenciales inválidas"
                ));
            }

            Persona persona = personaOpt.get();
            System.out.println("Usuario autenticado: " + persona.getCorreo() + ", Rol: " + persona.getRol());

            // Verificar rol (opcional)
            if (!"EMPLEADO".equalsIgnoreCase(persona.getRol())) {
                System.out.println("Usuario no es empleado: " + persona.getRol());
                return ResponseEntity.status(403).body(Map.of(
                        "error", "Acceso permitido solo para empleados"
                ));
            }

            // Generar token JWT
            String token = jwtUtil.generateToken(email);
            System.out.println("Token generado para: " + email);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "type", "Bearer",
                    "email", persona.getCorreo(),
                    "nombre", persona.getNombre(),
                    "rol", persona.getRol(),
                    "message", "Login exitoso"
            ));

        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error interno del servidor"
            ));
        }
    }
}