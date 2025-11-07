package com.gemini.gemini_ambiental.controller;

import com.gemini.gemini_ambiental.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            System.out.println("🔐 Login attempt - EMERGENCY MODE");

            String email = loginRequest.get("email");
            String dni = loginRequest.get("dni");

            // Validación básica
            if (email == null || dni == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Email y DNI requeridos"));
            }

            System.out.println("📧 Credenciales recibidas - Email: " + email + ", DNI: " + dni);

            // ✅ VERIFICACIÓN DIRECTA SIN BASE DE DATOS - SOLUCIÓN DE EMERGENCIA
            if ("admin@geminiambiental.com".equals(email) && "12345678".equals(dni)) {
                System.out.println("✅ Credenciales VÁLIDAS - Generando token");

                String token = jwtUtil.generateToken(email);

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("type", "Bearer");
                response.put("email", email);
                response.put("nombre", "Administrador");
                response.put("rol", "EMPLEADO");
                response.put("message", "Login exitoso (modo emergencia - sin BD)");

                System.out.println("🎫 Login EXITOSO - Token generado");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ Credenciales INVÁLIDAS");
                return ResponseEntity.status(401).body(createErrorResponse("Credenciales inválidas"));
            }

        } catch (Exception e) {
            System.err.println("💥 ERROR CRÍTICO en login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("Error interno: " + e.getMessage()));
        }
    }

    private Map<String, String> createErrorResponse(String error) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        return response;
    }
}