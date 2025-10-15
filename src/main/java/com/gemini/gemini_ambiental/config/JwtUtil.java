package com.gemini.gemini_ambiental.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; // Importar la clase Keys de jjwt-impl o jjwt-api
import org.springframework.stereotype.Component;

import java.security.Key; // Importar la clase Key estándar de Java
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // ✅ Clave secreta en formato Base64 seguro
    // Puedes generar una nueva con: Base64.getEncoder().encodeToString(new byte[32]);
    private final String SECRET_KEY = "bXlfc2VjcmV0X2tleV9mb3JfaHR0cF9iYXNpY19hdXRoX2FwcGxpY2F0aW9uX2Zvcl9zZXJ2aWNlX3VzZXJzX2FjY2Vzcw=="; // Ejemplo, cámbiala por una generada tú mismo

    // Método para obtener la clave de firma como un objeto Key de Java
    private Key getSignKey() {
        // Decodificar la clave Base64 y crear una clave HMAC
        byte[] keyBytes = java.util.Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // ✅ Usar el método getSignKey() para obtener la clave
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey()) // Asegura que la clave esté correctamente formateada
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        // ✅ Usar el método getSignKey() para firmar el token
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Asegura que la clave esté correctamente formateada
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}