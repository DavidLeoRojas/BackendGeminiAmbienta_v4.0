package com.gemini.gemini_ambiental.controller;

import com.gemini.gemini_ambiental.dto.PersonaDTO;
import com.gemini.gemini_ambiental.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @GetMapping
    public ResponseEntity<List<PersonaDTO>> getAllPersonas() {
        List<PersonaDTO> personas = personaService.getAllPersonas();
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonaDTO>> searchPersonas(@RequestParam String term) {
        List<PersonaDTO> personas = personaService.searchPersonas(term);
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/{dni}")
    public ResponseEntity<PersonaDTO> getPersonaByDni(@PathVariable String dni) {
        PersonaDTO persona = personaService.getPersonaByDni(dni);
        return ResponseEntity.ok(persona);
    }

    @PostMapping
    public ResponseEntity<PersonaDTO> createPersona(@RequestBody PersonaDTO personaDTO) {
        PersonaDTO createdPersona = personaService.createPersona(personaDTO);
        return ResponseEntity.ok(createdPersona);
    }

    @PutMapping("/{dni}")
    public ResponseEntity<PersonaDTO> updatePersona(@PathVariable String dni, @RequestBody PersonaDTO personaDTO) {
        PersonaDTO updatedPersona = personaService.updatePersona(dni, personaDTO);
        return ResponseEntity.ok(updatedPersona);
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> deletePersona(@PathVariable String dni) {
        personaService.deletePersona(dni);
        return ResponseEntity.noContent().build();
    }
}