package com.uade.tpo.controller;

import com.uade.tpo.dto.EstadisticaCreateDTO;
import com.uade.tpo.dto.EstadisticaDTO;
import com.uade.tpo.dto.UsuarioDTO;
import com.uade.tpo.model.Estadistica;
import com.uade.tpo.service.EstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaController {

    @Autowired
    private EstadisticaService estadisticaService;

    private EstadisticaDTO convertToDto(Estadistica estadistica) {
        EstadisticaDTO dto = new EstadisticaDTO();
        dto.setId(estadistica.getId());
        dto.setPartidoId(estadistica.getPartido().getId());
        dto.setAnotaciones(estadistica.getAnotaciones());
        dto.setAsistencias(estadistica.getAsistencias());
        dto.setAmonestaciones(estadistica.getAmonestaciones());
        dto.setMejorJugador(estadistica.isMejorJugador());

        UsuarioDTO usuarioDto = new UsuarioDTO();
        usuarioDto.setId(estadistica.getJugador().getId());
        usuarioDto.setUsername(estadistica.getJugador().getUsername());
        usuarioDto.setEmail(estadistica.getJugador().getEmail());
        dto.setJugador(usuarioDto);

        return dto;
    }

    @PostMapping
    public ResponseEntity<EstadisticaDTO> crearEstadistica(@RequestBody EstadisticaCreateDTO createDTO) {
        return estadisticaService.crearEstadistica(createDTO)
                .map(estadistica -> new ResponseEntity<>(convertToDto(estadistica), HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/partido/{partidoId}")
    public ResponseEntity<List<EstadisticaDTO>> obtenerEstadisticasPorPartido(@PathVariable Long partidoId) {
        List<Estadistica> estadisticas = estadisticaService.obtenerEstadisticasPorPartido(partidoId);
        List<EstadisticaDTO> dtos = estadisticas.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEstadistica(@PathVariable Long id) {
        estadisticaService.eliminarEstadistica(id);
        return ResponseEntity.noContent().build();
    }
}
