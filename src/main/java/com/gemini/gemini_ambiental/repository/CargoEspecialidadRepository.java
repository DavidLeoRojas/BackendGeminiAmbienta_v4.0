package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.CargoEspecialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// import java.util.UUID; // Ya no necesitas importar UUID

@Repository
public interface CargoEspecialidadRepository extends JpaRepository<CargoEspecialidad, String> { // Cambiado UUID a String
}