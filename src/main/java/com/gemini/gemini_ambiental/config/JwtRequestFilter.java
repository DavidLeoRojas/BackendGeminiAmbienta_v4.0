package com.gemini.gemini_ambiental.config;

import com.gemini.gemini_ambiental.service.PersonaService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private PersonaService personaService; // Asegúrate que este sea tu servicio personalizado

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // JWT Token está en el formato "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("No se puede obtener el nombre de usuario del token");
            } catch (ExpiredJwtException e) {
                System.out.println("El token ha expirado");
            }
        } else {
            logger.warn("JWT Token no comienza con Bearer");
        }

        // Una vez que obtenemos el token, validamos el usuario
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Cambio crucial: usar findByCorreo en lugar de loadUserByUsername
            com.gemini.gemini_ambiental.entity.Persona persona = this.personaService.findByCorreo(username).orElse(null);

            if (persona != null) {
                // Crea manualmente un UserDetails si es necesario
                // Asumiendo que tienes una clase como PersonaDetails implementando UserDetails
                // que reciba una entidad Persona en su constructor.
                com.gemini.gemini_ambiental.security.PersonaDetails userDetails = new com.gemini.gemini_ambiental.security.PersonaDetails(persona);

                // Validar el token con el UserDetails personalizado
                if (jwtUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            // Si persona es null, el usuario no existe, y no se autentica.
        }
        chain.doFilter(request, response);
    }
}