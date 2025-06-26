package com.uade.tpo.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.dto.PartidoCreateDTO;
import com.uade.tpo.model.Deporte;
import com.uade.tpo.model.NivelJuego;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.UsuarioDeporte;
import com.uade.tpo.repository.DeporteRepository;
import com.uade.tpo.repository.EquipoRepository;
import com.uade.tpo.repository.GeolocalizationRepository;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioRepository;
import com.uade.tpo.repository.UsuarioDeporteRepository;

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
    @Autowired
    private UsuarioDeporteRepository usuarioDeporteRepository;

    public Optional<Partido> crearPartido(PartidoCreateDTO dto) {
        // Validate cantidadJugadores is even and greater than 0
        if (dto.getCantidadJugadores() <= 0) {
            throw new RuntimeException("La cantidad de jugadores debe ser mayor a 0");
        }
        if (dto.getCantidadJugadores() % 2 != 0) {
            throw new RuntimeException("La cantidad de jugadores debe ser un número par");
        }

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

        // Save the partido first
        partido = partidoRepository.save(partido);

        // Create empty teams and add organizer to first team
        List<Equipo> equipos = new ArrayList<>();

        // Create first team and add organizer
        Equipo equipo1 = new Equipo();
        equipo1.setNombre("Equipo 1");
        equipo1.setJugadores(new ArrayList<>());
        equipo1.getJugadores().add(organizador);
        equipo1 = equipoRepository.save(equipo1);
        equipos.add(equipo1);

        // Create second team (empty)
        Equipo equipo2 = new Equipo();
        equipo2.setNombre("Equipo 2");
        equipo2.setJugadores(new ArrayList<>());
        equipo2 = equipoRepository.save(equipo2);
        equipos.add(equipo2);

        // Set teams to partido and save
        partido.setEquipos(equipos);
        partido = partidoRepository.save(partido);

        return Optional.of(partido);
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

    public void setEstrategiaEmparejamiento(Long partidoId, String estrategia) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado"));
        switch (estrategia) {
            case "EmparejarPorNivel" -> partido.setEstrategiaEmparejamiento(
                    new com.uade.tpo.model.emparejamientoStrategy.EmparejarPorNivel());
            case "EmparejarPorUbicacion" -> partido
                    .setEstrategiaEmparejamiento(new com.uade.tpo.model.emparejamientoStrategy.EmparejarPorUbicacion());
            case "EmparejarPorHistorial" -> partido
                    .setEstrategiaEmparejamiento(
                            new com.uade.tpo.model.emparejamientoStrategy.EmparejarPorHistorial());
            default -> throw new IllegalArgumentException("Estrategia no válida: " + estrategia);
        }
        partidoRepository.save(partido);
    }

    public void emparejarPartido(Long partidoId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado"));
        if (partido.getEstrategiaEmparejamiento() == null) {
            throw new IllegalArgumentException("El partido no tiene una estrategia de emparejamiento seteada");
        }
        if (partido
                .getEstrategiaEmparejamiento() instanceof com.uade.tpo.model.emparejamientoStrategy.EmparejarPorNivel nivelStrategy) {
            nivelStrategy.setUsuarioDeporteRepository(usuarioDeporteRepository);
            nivelStrategy.setEquipoRepository(equipoRepository);
        }
        partido.getEstrategiaEmparejamiento().emparejar(partido);

        // Validate if the required number of players has been reached
        int totalJugadoresNecesarios = partido.getCantidadJugadores();
        int totalJugadoresActuales = partido.getEquipos().stream()
                .mapToInt(equipo -> equipo.getJugadores() != null ? equipo.getJugadores().size() : 0)
                .sum();

        // If we have reached the required number of players, change state to
        // PartidoArmado
        if (totalJugadoresActuales >= totalJugadoresNecesarios) {
            partido.armar();
        }

        partidoRepository.save(partido);
    }

    public void cancelarPartido(Long partidoId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado"));
        partido.cancelar();
        partidoRepository.save(partido);
    }

    public void inscribirUsuario(Long partidoId, Long usuarioId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validate that the partido is in NecesitamosJugadores state
        if (partido.getEstado() == null ||
                !partido.getEstado().getClass().getSimpleName().equals("NecesitamosJugadores")) {
            String estadoActual = partido.getEstado() != null ? partido.getEstado().getClass().getSimpleName()
                    : "Sin estado";
            throw new IllegalArgumentException("No se puede inscribir al partido. Estado actual: " + estadoActual);
        }

        int totalJugadoresActuales = partido.getEquipos().stream()
                .mapToInt(equipo -> equipo.getJugadores() != null ? equipo.getJugadores().size() : 0)
                .sum();
        if (totalJugadoresActuales >= partido.getCantidadJugadores()) {
            throw new IllegalArgumentException("El partido ya alcanzó la cantidad máxima de jugadores");
        }

        boolean usuarioYaInscrito = partido.getEquipos().stream()
                .anyMatch(equipo -> equipo.getJugadores() != null &&
                        equipo.getJugadores().stream().anyMatch(jugador -> jugador.getId().equals(usuarioId)));
        if (usuarioYaInscrito) {
            throw new IllegalArgumentException("El usuario ya está inscrito en este partido");
        }

        UsuarioDeporte usuarioDeporte = usuarioDeporteRepository.findByUsuarioAndDeporte(usuario, partido.getDeporte())
                .orElseThrow(() -> new IllegalArgumentException("El usuario no practica este deporte"));

        NivelJuego nivelUsuario = usuarioDeporte.getNivelDeJuego();
        if (nivelUsuario.ordinal() < partido.getNivelMinimo().ordinal() ||
                nivelUsuario.ordinal() > partido.getNivelMaximo().ordinal()) {
            throw new IllegalArgumentException("El nivel del usuario no cumple con los requisitos del partido");
        }

        List<Equipo> equipos = partido.getEquipos();
        if (equipos == null || equipos.isEmpty()) {
            throw new IllegalArgumentException("El partido no tiene equipos configurados");
        }

        Equipo equipoElegido = equipos.get(0);
        int menorCantidad = equipoElegido.getJugadores() != null ? equipoElegido.getJugadores().size() : 0;

        for (Equipo equipo : equipos) {
            int cantidadJugadores = equipo.getJugadores() != null ? equipo.getJugadores().size() : 0;
            if (cantidadJugadores < menorCantidad) {
                equipoElegido = equipo;
                menorCantidad = cantidadJugadores;
            }
        }

        if (equipoElegido.getJugadores() == null) {
            equipoElegido.setJugadores(new ArrayList<>());
        }
        equipoElegido.getJugadores().add(usuario);
        equipoRepository.save(equipoElegido);

        totalJugadoresActuales = partido.getEquipos().stream()
                .mapToInt(equipo -> equipo.getJugadores() != null ? equipo.getJugadores().size() : 0)
                .sum();

        if (totalJugadoresActuales >= partido.getCantidadJugadores()) {
            partido.armar();
        }

        partidoRepository.save(partido);
    }
}
