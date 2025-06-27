package com.uade.tpo.controller;

import com.uade.tpo.dto.EquipoCreateDTO;
import com.uade.tpo.dto.EquipoDTO;
import com.uade.tpo.dto.JugadorSimpleDTO;
import com.uade.tpo.dto.UsuarioDTO;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    private JugadorSimpleDTO convertJugadorToSimpleDto(com.uade.tpo.model.Usuario usuario) {
        JugadorSimpleDTO dto = new JugadorSimpleDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        return dto;
    }

    private EquipoDTO convertToDto(Equipo equipo) {
        EquipoDTO dto = new EquipoDTO();
        dto.setId(equipo.getId());
        dto.setNombre(equipo.getNombre());
        if (equipo.getJugadores() != null) {
            dto.setJugadores(
                    equipo.getJugadores().stream().map(this::convertJugadorToSimpleDto).collect(Collectors.toList()));
        }
        return dto;
    }

    @PostMapping
    public ResponseEntity<EquipoDTO> crearEquipo(@RequestBody EquipoCreateDTO equipoCreateDTO) {
        Equipo nuevoEquipo = equipoService.crearEquipo(equipoCreateDTO);
        return new ResponseEntity<>(convertToDto(nuevoEquipo), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EquipoDTO>> obtenerEquipos() {
        List<Equipo> equipos = equipoService.obtenerEquipos();
        List<EquipoDTO> equipoDTOs = equipos.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(equipoDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoDTO> obtenerEquipoPorId(@PathVariable Long id) {
        Optional<Equipo> equipo = equipoService.obtenerEquipoPorId(id);
        return equipo.map(e -> ResponseEntity.ok(convertToDto(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipoDTO> modificarEquipo(@PathVariable Long id,
            @RequestBody EquipoCreateDTO equipoCreateDTO) {
        Equipo equipoActualizado = equipoService.modificarEquipo(id, equipoCreateDTO);
        if (equipoActualizado != null) {
            return ResponseEntity.ok(convertToDto(equipoActualizado));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEquipo(@PathVariable Long id) {
        equipoService.eliminarEquipo(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{equipoId}/jugadores/{usuarioId}")
    public ResponseEntity<EquipoDTO> agregarJugadorAEquipo(@PathVariable Long equipoId, @PathVariable Long usuarioId) {
        Equipo equipoActualizado = equipoService.agregarJugador(equipoId, usuarioId);
        if (equipoActualizado != null) {
            return ResponseEntity.ok(convertToDto(equipoActualizado));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{equipoId}/jugadores/{usuarioId}")
    public ResponseEntity<EquipoDTO> eliminarJugadorDeEquipo(@PathVariable Long equipoId,
            @PathVariable Long usuarioId) {
        Equipo equipoActualizado = equipoService.eliminarJugador(equipoId, usuarioId);
        if (equipoActualizado != null) {
            return ResponseEntity.ok(convertToDto(equipoActualizado));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
