package com.uade.tpo.service;

import com.uade.tpo.dto.ComentarioCreateDTO;
import com.uade.tpo.model.Comentario;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.repository.ComentarioRepository;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PartidoRepository partidoRepository;

    public Optional<Comentario> crearComentario(ComentarioCreateDTO dto) {
        Optional<Usuario> usuario = usuarioRepository.findById(dto.getJugadorId());
        Optional<Partido> partido = partidoRepository.findById(dto.getPartidoId());

        if (usuario.isPresent() && partido.isPresent()) {
            Comentario comentario = new Comentario();
            comentario.setJugador(usuario.get());
            comentario.setPartido(partido.get());
            comentario.setMensaje(dto.getMensaje());
            comentario.setFecha(new Date());
            return Optional.of(comentarioRepository.save(comentario));
        }
        return Optional.empty();
    }

    public List<Comentario> obtenerComentariosPorPartido(Long partidoId) {
        return comentarioRepository.findByPartidoId(partidoId);
    }

    public void eliminarComentario(Long id) {
        comentarioRepository.deleteById(id);
    }
}
