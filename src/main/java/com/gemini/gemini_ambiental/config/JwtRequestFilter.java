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
    private PersonaService personaService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // ✅ EXCLUIR ENDPOINTS PÚBLICOS
        String path = request.getServletPath();
        if (isPublicEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        // JWT Token está en el formato "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);

                // ✅ Validación adicional del token
                if (!jwtUtil.validateToken(jwtToken)) {
                    logger.warn("Token JWT inválido o expirado");
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
                    return;
                }
            } catch (Exception e) {
                logger.warn("Error procesando JWT token: " + e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Error en el token");
                return;
            }
        } else {
            logger.warn("JWT Token no comienza con Bearer o está ausente");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT requerido");
            return;
        }

        // Validar usuario en la base de datos
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            com.gemini.gemini_ambiental.entity.Persona persona = this.personaService.findByCorreo(username).orElse(null);

            if (persona != null) {
                com.gemini.gemini_ambiental.security.PersonaDetails userDetails =
                        new com.gemini.gemini_ambiental.security.PersonaDetails(persona);

                // ✅ Usar validación mejorada
                if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/error") ||
                path.equals("/") ||
                path.startsWith("/swagger") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/webjars/");
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}