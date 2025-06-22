package com.uade.tpo.service;

import com.uade.tpo.dto.EstadisticaCreateDTO;
import com.uade.tpo.model.Estadistica;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.repository.EstadisticaRepository;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadisticaService {

    @Autowired
    private EstadisticaRepository estadisticaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PartidoRepository partidoRepository;

    public Optional<Estadistica> crearEstadistica(EstadisticaCreateDTO dto) {
        Optional<Usuario> usuario = usuarioRepository.findById(dto.getJugadorId());
        Optional<Partido> partido = partidoRepository.findById(dto.getPartidoId());

        if (usuario.isPresent() && partido.isPresent()) {
            Estadistica estadistica = new Estadistica();
            estadistica.setJugador(usuario.get());
            estadistica.setPartido(partido.get());
            estadistica.setAnotaciones(dto.getAnotaciones());
            estadistica.setAsistencias(dto.getAsistencias());
            estadistica.setAmonestaciones(dto.getAmonestaciones());
            estadistica.setMejorJugador(dto.isMejorJugador());
            return Optional.of(estadisticaRepository.save(estadistica));
        }
        return Optional.empty();
    }

    public List<Estadistica> obtenerEstadisticasPorPartido(Long partidoId) {
        return estadisticaRepository.findByPartidoId(partidoId);
    }

    public void eliminarEstadistica(Long id) {
        estadisticaRepository.deleteById(id);
    }
}
