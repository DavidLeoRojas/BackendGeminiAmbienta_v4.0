// src/main/java/com/gemini/gemini_ambiental/repository/CategoriaServicioRepository.java
package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.CategoriaServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaServicioRepository extends JpaRepository<CategoriaServicio, String> {
    // Puedes añadir métodos de consulta personalizados aquí si los necesitas
}