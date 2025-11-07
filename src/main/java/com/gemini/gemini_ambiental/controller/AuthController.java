package com.gemini.gemini_ambiental.controller;

import com.gemini.gemini_ambiental.config.JwtUtil;
import com.gemini.gemini_ambiental.entity.Persona;
import com.gemini.gemini_ambiental.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PersonaRepository personaRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> loginRequest) {

        String correo = loginRequest.get("correo");
        String dni = loginRequest.get("dni");
        String password = loginRequest.get("password");

        Persona persona = personaRepository.findByCorreoAndDni(correo, dni)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!persona.getRol().equalsIgnoreCase("EMPLEADO")) {
            return ResponseEntity.status(403).body("Acceso permitido solo para empleados");
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(correo, password)
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String token = jwtUtil.generateToken(correo);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "nombre", persona.getNombre(),
                "rol", persona.getRol()
        ));
    }
}
