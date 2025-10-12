package com.gemini.gemini_ambiental.repository;

import com.gemini.gemini_ambiental.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {

    Optional<Persona> findByDni(String dni);

    Optional<Persona> findByCorreo(String correo);

    List<Persona> findByNombreContainingIgnoreCase(String nombre);

    List<Persona> findByRol(String rol);

    List<Persona> findByTipoPersona(String tipoPersona);

    @Query("SELECT p FROM Persona p WHERE p.correo LIKE %:search% OR p.nombre LIKE %:search% OR p.dni LIKE %:search%")
    List<Persona> searchByTerm(@Param("search") String search);

    @Query("SELECT COUNT(p) FROM Persona p WHERE p.rol = 'Cliente'")
    Long countClientes();

    @Query("SELECT COUNT(p) FROM Persona p WHERE p.rol = 'Empleado'")
    Long countEmpleados();
}