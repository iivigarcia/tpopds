package com.uade.tpo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.dto.PartidoCreateDTO;
import com.uade.tpo.model.Deporte;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.EquipoJugador;
import com.uade.tpo.model.EquipoJugadorId;
import com.uade.tpo.model.NivelJuego;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.model.UsuarioDeporte;
import com.uade.tpo.repository.DeporteRepository;
import com.uade.tpo.repository.EquipoJugadorRepository;
import com.uade.tpo.repository.EquipoRepository;
import com.uade.tpo.repository.GeolocalizationRepository;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioDeporteRepository;
import com.uade.tpo.repository.UsuarioRepository;

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
    @Autowired
    private EquipoJugadorRepository equipoJugadorRepository;

    public Optional<Partido> crearPartido(PartidoCreateDTO dto) {
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

        partido = partidoRepository.save(partido);

        List<Equipo> equipos = new ArrayList<>();

        Equipo equipo1 = new Equipo();
        equipo1.setNombre("Equipo 1");
        equipo1.setJugadores(new ArrayList<>());
        equipo1 = equipoRepository.save(equipo1);
        equipos.add(equipo1);

        Equipo equipo2 = new Equipo();
        equipo2.setNombre("Equipo 2");
        equipo2.setJugadores(new ArrayList<>());
        equipo2 = equipoRepository.save(equipo2);
        equipos.add(equipo2);

        partido.setEquipos(equipos);
        partido = partidoRepository.save(partido);

        EquipoJugador participacionOrganizador = new EquipoJugador();
        EquipoJugadorId participacionOrganizadorId = new EquipoJugadorId();
        participacionOrganizadorId.setEquipoId(equipo1.getId());
        participacionOrganizadorId.setUsuarioId(organizador.getId());
        participacionOrganizador.setId(participacionOrganizadorId);
        participacionOrganizador.setUsuario(organizador);
        participacionOrganizador.setEquipo(equipo1);
        participacionOrganizador.setInscrito(true);
        participacionOrganizador.setConfirmado(true); // Organizer is confirmed by default
        equipoJugadorRepository.save(participacionOrganizador);

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
        if (partido
                .getEstrategiaEmparejamiento() instanceof com.uade.tpo.model.emparejamientoStrategy.EmparejarPorUbicacion ubicacionStrategy) {
            ubicacionStrategy.setUsuarioDeporteRepository(usuarioDeporteRepository);
            ubicacionStrategy.setEquipoRepository(equipoRepository);
        }
        if (partido
                .getEstrategiaEmparejamiento() instanceof com.uade.tpo.model.emparejamientoStrategy.EmparejarPorHistorial historialStrategy) {
            historialStrategy.setUsuarioDeporteRepository(usuarioDeporteRepository);
            historialStrategy.setEquipoRepository(equipoRepository);
            historialStrategy.setPartidoRepository(partidoRepository);
            historialStrategy.setEquipoJugadorRepository(equipoJugadorRepository);
        }
        partido.getEstrategiaEmparejamiento().emparejar(partido);

        int totalJugadoresNecesarios = partido.getCantidadJugadores();
        int totalJugadoresActuales = partido.getEquipos().stream()
                .mapToInt(equipo -> equipo.getJugadores() != null ? equipo.getJugadores().size() : 0)
                .sum();

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

        if (partido.getEstado() == null ||
                !partido.getEstado().getClass().getSimpleName().equals("NecesitamosJugadores")) {
            String estadoActual = partido.getEstado() != null ? partido.getEstado().getClass().getSimpleName()
                    : "Sin estado";
            throw new IllegalArgumentException("No se puede inscribir al partido. Estado actual: " + estadoActual);
        }

        boolean usuarioYaInscrito = partido.getEquipos().stream()
                .anyMatch(equipo -> equipoJugadorRepository.findByEquipoAndUsuario(equipo, usuario).isPresent());
        if (usuarioYaInscrito) {
            throw new IllegalArgumentException("El usuario ya está inscrito en este partido");
        }

        int totalJugadoresActuales = partido.getEquipos().stream()
                .mapToInt(equipo -> equipoJugadorRepository.findByEquipo(equipo).size())
                .sum();
        if (totalJugadoresActuales >= partido.getCantidadJugadores()) {
            throw new IllegalArgumentException("El partido ya alcanzó la cantidad máxima de jugadores");
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
        int menorCantidad = equipoJugadorRepository.findByEquipo(equipoElegido).size();

        for (Equipo equipo : equipos) {
            int cantidadJugadores = equipoJugadorRepository.findByEquipo(equipo).size();
            if (cantidadJugadores < menorCantidad) {
                equipoElegido = equipo;
                menorCantidad = cantidadJugadores;
            }
        }

        EquipoJugador participacion = new EquipoJugador();
        EquipoJugadorId participacionId = new EquipoJugadorId();
        participacionId.setEquipoId(equipoElegido.getId());
        participacionId.setUsuarioId(usuario.getId());
        participacion.setId(participacionId);
        participacion.setUsuario(usuario);
        participacion.setEquipo(equipoElegido);
        participacion.setInscrito(true);
        participacion.setConfirmado(false); // Not confirmed by default
        equipoJugadorRepository.save(participacion);

        totalJugadoresActuales = partido.getEquipos().stream()
                .mapToInt(equipo -> equipoJugadorRepository.findByEquipo(equipo).size())
                .sum();

        if (totalJugadoresActuales >= partido.getCantidadJugadores()) {
            partido.armar();
        }

        partidoRepository.save(partido);
    }

    public void confirmarParticipacion(Long partidoId, Long usuarioId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Optional<EquipoJugador> participacionOpt = partido.getEquipos().stream()
                .map(equipo -> equipoJugadorRepository.findByEquipoAndUsuario(equipo, usuario))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        EquipoJugador participacion = participacionOpt
                .orElseThrow(() -> new IllegalArgumentException("El usuario no está inscrito en este partido"));

        if (!participacion.isInscrito()) {
            throw new IllegalArgumentException("El usuario no está inscrito en este partido");
        }

        if (participacion.isConfirmado()) {
            throw new IllegalArgumentException("El usuario ya confirmó su participación");
        }

        participacion.setConfirmado(true);
        equipoJugadorRepository.save(participacion);

        int totalJugadoresConfirmados = partido.getEquipos().stream()
                .mapToInt(equipo -> equipoJugadorRepository.findByEquipo(equipo).stream()
                        .filter(EquipoJugador::isConfirmado)
                        .mapToInt(ej -> 1)
                        .sum())
                .sum();

        int totalJugadoresNecesarios = partido.getCantidadJugadores();

        if (totalJugadoresConfirmados >= totalJugadoresNecesarios) {
            partido.confirmar();
            partidoRepository.save(partido);
        }
    }

    public void confirmarTodosLosJugadores(Long partidoId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado"));

        List<EquipoJugador> participaciones = partido.getEquipos().stream()
                .flatMap(equipo -> equipoJugadorRepository.findByEquipo(equipo).stream())
                .collect(Collectors.toList());

        if (participaciones.isEmpty()) {
            throw new IllegalArgumentException("El partido no tiene jugadores inscritos");
        }

        for (EquipoJugador participacion : participaciones) {
            if (participacion.isInscrito() && !participacion.isConfirmado()) {
                participacion.setConfirmado(true);
                equipoJugadorRepository.save(participacion);
            }
        }

        int totalJugadoresConfirmados = participaciones.stream()
                .filter(EquipoJugador::isConfirmado)
                .mapToInt(ej -> 1)
                .sum();

        int totalJugadoresNecesarios = partido.getCantidadJugadores();

        if (totalJugadoresConfirmados >= totalJugadoresNecesarios) {
            partido.confirmar();
            partidoRepository.save(partido);
        }
    }

    public void comenzarPartido(Long partidoId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado"));

        if (partido.getEstado() == null ||
                !partido.getEstado().getClass().getSimpleName().equals("Confirmado")) {
            String estadoActual = partido.getEstado() != null ? partido.getEstado().getClass().getSimpleName()
                    : "Sin estado";
            throw new IllegalArgumentException(
                    "El partido debe estar en estado 'Confirmado' para poder comenzar. Estado actual: " + estadoActual);
        }

        partido.comenzar();
        partidoRepository.save(partido);
    }

    public void finalizarPartido(Long partidoId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado"));

        partido.finalizar();
        partidoRepository.save(partido);
    }
}
