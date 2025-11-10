package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.TipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoServicioRepository extends JpaRepository<TipoServicio, String> {
}