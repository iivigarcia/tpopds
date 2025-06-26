package com.uade.tpo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.dto.ComentarioCreateDTO;
import com.uade.tpo.model.Comentario;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.repository.ComentarioRepository;
import com.uade.tpo.repository.EquipoJugadorRepository;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioRepository;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PartidoRepository partidoRepository;
    @Autowired
    private EquipoJugadorRepository equipoJugadorRepository;

    public Optional<Comentario> crearComentario(ComentarioCreateDTO dto) {
        Partido partido = partidoRepository.findById(dto.getPartidoId())
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        if (partido.getEstado() == null ||
                !partido.getEstado().getClass().getSimpleName().equals("Finalizado")) {
            throw new RuntimeException("Solo se pueden agregar comentarios a partidos finalizados");
        }

        Usuario usuario = usuarioRepository.findById(dto.getJugadorId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean participoEnPartido = partido.getEquipos().stream()
                .anyMatch(equipo -> equipoJugadorRepository.findByEquipoAndUsuario(equipo, usuario).isPresent());

        if (!participoEnPartido) {
            throw new RuntimeException("Solo los jugadores que participaron del partido pueden agregar comentarios");
        }

        if (dto.getMensaje() == null || dto.getMensaje().trim().isEmpty()) {
            throw new RuntimeException("El mensaje no puede estar vacío");
        }

        Comentario comentario = new Comentario();
        comentario.setJugador(usuario);
        comentario.setPartido(partido);
        comentario.setMensaje(dto.getMensaje().trim());
        comentario.setFecha(new java.util.Date());

        return Optional.of(comentarioRepository.save(comentario));
    }

    public List<Comentario> obtenerComentariosPorPartido(Long partidoId) {
        if (!partidoRepository.existsById(partidoId)) {
            throw new RuntimeException("Partido no encontrado");
        }

        return comentarioRepository.findByPartidoId(partidoId);
    }

    public void eliminarComentario(Long id) {
        comentarioRepository.deleteById(id);
    }
}
