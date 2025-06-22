package com.uade.tpo.controller;

import com.uade.tpo.dto.DeporteCreateDTO;
import com.uade.tpo.dto.DeporteDTO;
import com.uade.tpo.model.Deporte;
import com.uade.tpo.service.DeporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deportes")
public class DeporteController {

    @Autowired
    private DeporteService deporteService;

    private DeporteDTO convertToDto(Deporte deporte) {
        DeporteDTO dto = new DeporteDTO();
        dto.setId(deporte.getId());
        dto.setNombre(deporte.getNombre());
        return dto;
    }

    @PostMapping
    public ResponseEntity<DeporteDTO> crearDeporte(@RequestBody DeporteCreateDTO deporteCreateDTO) {
        Deporte nuevoDeporte = deporteService.crearDeporte(deporteCreateDTO);
        return new ResponseEntity<>(convertToDto(nuevoDeporte), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DeporteDTO>> obtenerDeportes() {
        List<Deporte> deportes = deporteService.obtenerDeportes();
        List<DeporteDTO> deporteDTOs = deportes.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(deporteDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeporteDTO> obtenerDeportePorId(@PathVariable Long id) {
        Optional<Deporte> deporte = deporteService.obtenerDeportePorId(id);
        return deporte.map(d -> ResponseEntity.ok(convertToDto(d)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeporteDTO> modificarDeporte(@PathVariable Long id,
            @RequestBody DeporteCreateDTO deporteCreateDTO) {
        Deporte deporteActualizado = deporteService.modificarDeporte(id, deporteCreateDTO);
        if (deporteActualizado != null) {
            return ResponseEntity.ok(convertToDto(deporteActualizado));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDeporte(@PathVariable Long id) {
        deporteService.eliminarDeporte(id);
        return ResponseEntity.noContent().build();
    }
}
