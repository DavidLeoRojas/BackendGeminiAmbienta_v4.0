package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// import java.util.UUID; // Ya no necesitas importar UUID

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, String> { // Cambiado UUID a String
}