package com.gemini.gemini_ambiental.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret:bXlfc2VjcmV0X2tleV9mb3JfaHR0cF9iYXNpY19hdXRoX2FwcGxpY2F0aW9uX2Zvcl9zZXJ2aWNlX3VzZXJzX2FjY2Vzcw==}")
    private String SECRET_KEY;

    @Value("${jwt.expiration:36000000}")
    private long EXPIRATION_TIME;

    private Key getSignKey() {
        try {
            byte[] keyBytes;

            if (SECRET_KEY.length() < 32) {
                keyBytes = new byte[32];
                byte[] originalBytes = SECRET_KEY.getBytes();
                System.arraycopy(originalBytes, 0, keyBytes, 0, Math.min(originalBytes.length, 32));
            } else {
                try {
                    keyBytes = java.util.Base64.getDecoder().decode(SECRET_KEY);
                } catch (IllegalArgumentException e) {
                    keyBytes = SECRET_KEY.getBytes();
                }
            }

            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            System.err.println("Error con la clave JWT, generando automáticamente: " + e.getMessage());
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
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
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.err.println("Error extrayendo claims del token: " + e.getMessage());
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            System.err.println("Error verificando expiración: " + e.getMessage());
            return true;
        }
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername != null &&
                    extractedUsername.equals(username) &&
                    !isTokenExpired(token));
        } catch (Exception e) {
            System.err.println("Error validando token: " + e.getMessage());
            return false;
        }
    }

    public Boolean validateToken(String token) {
        try {
            final String username = extractUsername(token);
            return (username != null && !isTokenExpired(token));
        } catch (Exception e) {
            System.err.println("Error validando token: " + e.getMessage());
            return false;
        }
    }
}