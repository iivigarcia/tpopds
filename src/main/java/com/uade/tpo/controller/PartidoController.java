package com.uade.tpo.controller;

import com.uade.tpo.dto.*;
import com.uade.tpo.model.Comentario;
import com.uade.tpo.model.Estadistica;
import com.uade.tpo.model.Partido;
import com.uade.tpo.service.PartidoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    @Autowired
    private PartidoService partidoService;

    private UsuarioDTO convertUsuarioToDto(com.uade.tpo.model.Usuario usuario) {
        if (usuario == null)
            return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        return dto;
    }

    private EquipoDTO convertEquipoToDto(com.uade.tpo.model.Equipo equipo) {
        if (equipo == null)
            return null;
        EquipoDTO dto = new EquipoDTO();
        dto.setId(equipo.getId());
        dto.setNombre(equipo.getNombre());
        return dto;
    }

    private ComentarioDTO convertComentarioToDto(Comentario comentario) {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setId(comentario.getId());
        dto.setMensaje(comentario.getMensaje());
        dto.setFecha(comentario.getFecha());
        dto.setPartidoId(comentario.getPartido().getId());
        dto.setJugador(convertUsuarioToDto(comentario.getJugador()));
        return dto;
    }

    private EstadisticaDTO convertEstadisticaToDto(Estadistica estadistica) {
        EstadisticaDTO dto = new EstadisticaDTO();
        dto.setId(estadistica.getId());
        dto.setPartidoId(estadistica.getPartido().getId());
        dto.setJugador(convertUsuarioToDto(estadistica.getJugador()));
        dto.setAnotaciones(estadistica.getAnotaciones());
        dto.setAsistencias(estadistica.getAsistencias());
        dto.setAmonestaciones(estadistica.getAmonestaciones());
        dto.setMejorJugador(estadistica.isMejorJugador());
        return dto;
    }

    private PartidoDTO convertToDto(Partido partido) {
        PartidoDTO dto = new PartidoDTO();
        dto.setId(partido.getId());
        dto.setFecha(partido.getFecha());
        dto.setHora(partido.getHora());
        dto.setOrganizador(convertUsuarioToDto(partido.getOrganizador()));
        dto.setUbicacion(partido.getGeolocalizationId());
        dto.setEstadoPartido(partido.getEstado() != null ? partido.getEstado().getClass().getSimpleName() : null);
        dto.setEstrategiaEmparejamiento(partido.getEstrategiaEmparejamiento() != null
                ? partido.getEstrategiaEmparejamiento().getClass().getSimpleName()
                : null);

        if (partido.getEquipos() != null) {
            dto.setEquipos(
                    partido.getEquipos().stream().map(this::convertEquipoToDto).collect(Collectors.toList()));
        }

        if (partido.getComentarios() != null) {
            dto.setComentarios(
                    partido.getComentarios().stream().map(this::convertComentarioToDto).collect(Collectors.toList()));
        }
        if (partido.getEstadisticas() != null) {
            dto.setEstadisticas(
                    partido.getEstadisticas().stream().map(this::convertEstadisticaToDto).collect(Collectors.toList()));
        }
        return dto;
    }

    @PostMapping
    public ResponseEntity<PartidoDTO> crearPartido(@RequestBody PartidoCreateDTO createDTO) {
        return partidoService.crearPartido(createDTO)
                .map(partido -> new ResponseEntity<>(convertToDto(partido), HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    public ResponseEntity<List<PartidoDTO>> obtenerPartidos() {
        List<Partido> partidos = partidoService.obtenerPartidos();
        List<PartidoDTO> dtos = partidos.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidoDTO> obtenerPartidoPorId(@PathVariable Long id) {
        Optional<Partido> partido = partidoService.obtenerPartidoPorId(id);
        return partido.map(p -> ResponseEntity.ok(convertToDto(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ubicacion/{ubicacionId}/incompletos")
    public ResponseEntity<List<PartidoDTO>> obtenerPartidosIncompletosPorUbicacion(@PathVariable Integer ubicacionId) {
        List<Partido> partidos = partidoService.obtenerPartidosIncompletosPorUbicacion(ubicacionId);
        List<PartidoDTO> dtos = partidos.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPartido(@PathVariable Long id) {
        partidoService.eliminarPartido(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/estrategia")
    public ResponseEntity<String> setEstrategia(@PathVariable Long id, @RequestParam String estrategia) {
        try {
            partidoService.setEstrategiaEmparejamiento(id, estrategia);
            return ResponseEntity.ok("Estrategia de emparejamiento actualizada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar la estrategia: " + e.getMessage());
        }
    }

}
