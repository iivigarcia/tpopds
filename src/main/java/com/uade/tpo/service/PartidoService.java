package com.uade.tpo.service;

import com.uade.tpo.dto.PartidoCreateDTO;
import com.uade.tpo.model.Deporte;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.Geolocalization;
import com.uade.tpo.model.NivelJuego;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.model.emparejamientoStrategy.EmparejamientoStrategy;
import com.uade.tpo.repository.DeporteRepository;
import com.uade.tpo.repository.EquipoRepository;
import com.uade.tpo.repository.GeolocalizationRepository;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioDeporteRepository;
import com.uade.tpo.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartidoService {

    @Autowired
    private PartidoRepository partidoRepository;
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private GeolocalizationRepository geolocalizationRepository;
    @Autowired
    private DeporteRepository deporteRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Optional<Partido> crearPartido(PartidoCreateDTO dto) {
        Deporte deporte = deporteRepository.findById(dto.getDeporteId())
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado"));

        if (!geolocalizationRepository.existsById(dto.getUbicacionId())) {
            throw new RuntimeException("Ubicación no encontrada");
        }

        Usuario organizador = usuarioRepository.findById(dto.getOrganizadorId())
                .orElseThrow(() -> new RuntimeException("Organizador no encontrado"));

        Partido partido = new Partido();
        partido.setDeporte(deporte);
        partido.setFecha(dto.getFecha());
        partido.setHora(dto.getHora());
        partido.setGeolocalizationId(dto.getUbicacionId());
        partido.setOrganizador(organizador);
        partido.setCantidadJugadores(dto.getCantidadJugadores());
        partido.setNivelMinimo(NivelJuego.valueOf(dto.getNivelMinimo()));
        partido.setNivelMaximo(NivelJuego.valueOf(dto.getNivelMaximo()));

        return Optional.of(partidoRepository.save(partido));
    }

    public List<Partido> obtenerPartidos() {
        return partidoRepository.findAll();
    }

    public Optional<Partido> obtenerPartidoPorId(Long id) {
        return partidoRepository.findById(id);
    }

    public List<Partido> obtenerPartidosIncompletosPorUbicacion(Integer ubicacionId) {
        if (!geolocalizationRepository.existsById(ubicacionId)) {
            throw new RuntimeException("Ubicación no encontrada");
        }

        List<Partido> partidos = partidoRepository.findAll();

        return partidos.stream()
                .filter(partido -> partido.getGeolocalizationId().equals(ubicacionId))
                .filter(this::esPartidoIncompleto)
                .toList();
    }

    private boolean esPartidoIncompleto(Partido partido) {
        if (partido.getEquipos() == null || partido.getEquipos().isEmpty()) {
            return true;
        }

        int totalJugadoresNecesarios = partido.getCantidadJugadores();
        int totalJugadoresActuales = partido.getEquipos().stream()
                .mapToInt(equipo -> equipo.getJugadores() != null ? equipo.getJugadores().size() : 0)
                .sum();

        return totalJugadoresActuales < totalJugadoresNecesarios;
    }

    public void eliminarPartido(Long id) {
        partidoRepository.deleteById(id);
    }
}
