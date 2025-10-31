package com.gemini.gemini_ambiental;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableScheduling
@EnableMethodSecurity
@SpringBootApplication
public class GeminiAmbientalApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeminiAmbientalApplication.class, args);
	}
}