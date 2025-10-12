package com.gemini.gemini_ambiental.repository;


import com.gemini.gemini_ambiental.entity.CargoEspecialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoEspecialidadRepository extends JpaRepository<CargoEspecialidad, String> {
    // Métodos adicionales si son necesarios
}
