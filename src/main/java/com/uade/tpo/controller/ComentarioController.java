package com.uade.tpo.controller;

import com.uade.tpo.dto.ComentarioCreateDTO;
import com.uade.tpo.dto.ComentarioDTO;
import com.uade.tpo.dto.UsuarioDTO;
import com.uade.tpo.model.Comentario;
import com.uade.tpo.service.ComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    private ComentarioDTO convertToDto(Comentario comentario) {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setId(comentario.getId());
        dto.setMensaje(comentario.getMensaje());
        dto.setFecha(comentario.getFecha());
        dto.setPartidoId(comentario.getPartido().getId());

        UsuarioDTO usuarioDto = new UsuarioDTO();
        usuarioDto.setId(comentario.getJugador().getId());
        usuarioDto.setUsername(comentario.getJugador().getUsername());
        usuarioDto.setEmail(comentario.getJugador().getEmail());
        dto.setJugador(usuarioDto);

        return dto;
    }

    @PostMapping
    public ResponseEntity<ComentarioDTO> crearComentario(@RequestBody ComentarioCreateDTO createDTO) {
        return comentarioService.crearComentario(createDTO)
                .map(comentario -> new ResponseEntity<>(convertToDto(comentario), HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/partido/{partidoId}")
    public ResponseEntity<List<ComentarioDTO>> obtenerComentariosPorPartido(@PathVariable Long partidoId) {
        List<Comentario> comentarios = comentarioService.obtenerComentariosPorPartido(partidoId);
        List<ComentarioDTO> dtos = comentarios.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarComentario(@PathVariable Long id) {
        comentarioService.eliminarComentario(id);
        return ResponseEntity.noContent().build();
    }
}
