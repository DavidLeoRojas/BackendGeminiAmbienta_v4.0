package com.gemini.gemini_ambiental.controller;

import com.gemini.gemini_ambiental.config.JwtUtil;
import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
            System.out.println("🔐 Login attempt received");

            String email = loginRequest.get("email");
            String dni = loginRequest.get("dni");

            if (email == null || email.trim().isEmpty() || dni == null || dni.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Email y DNI son requeridos"));
            }

            System.out.println("📧 Buscando usuario: " + email);

            // ✅ CONSULTA DIRECTA Y SEGURA
            Optional<Persona> personaOpt = personaRepository.findByCorreoAndDni(email, dni);

            if (!personaOpt.isPresent()) {
                System.out.println("❌ Credenciales inválidas para: " + email);
                return ResponseEntity.status(401).body(createErrorResponse("Credenciales inválidas"));
            }

            Persona persona = personaOpt.get();
            System.out.println("✅ Usuario autenticado: " + persona.getNombre());

            if (!"EMPLEADO".equalsIgnoreCase(persona.getRol())) {
                System.out.println("❌ Usuario no es empleado: " + persona.getRol());
                return ResponseEntity.status(403).body(createErrorResponse("Acceso permitido solo para empleados"));
            }

            String token = jwtUtil.generateToken(email);
            System.out.println("🎫 Token generado para: " + email);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("email", persona.getCorreo());
            response.put("nombre", persona.getNombre());
            response.put("rol", persona.getRol());
            response.put("message", "Login exitoso");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("💥 ERROR en login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("Error interno del servidor"));
        }
    }

    private Map<String, String> createErrorResponse(String error) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        return response;
    }
}