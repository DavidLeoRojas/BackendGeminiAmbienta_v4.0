package com.gemini.gemini_ambiental.repository;



import com.gemini.gemini_ambiental.entity.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, String> {
    // Métodos adicionales si son necesarios
}