// src/main/java/com/gemini/gemini_ambiental/config/CorsConfig.java

package com.gemini.gemini_ambiental.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:5500",
                                "http://127.0.0.1:5500",
                                "https://david-gemini.github.io"  // 👈 Reemplaza esto con tu URL real de GitHub Pages si es diferente
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false); // Mantén false si no usas cookies ni autenticación basada en credenciales
            }
        };
    }
}