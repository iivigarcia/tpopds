package com.uade.tpo.model.emparejamientoStrategy;

import com.uade.tpo.model.*;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioDeporteRepository;
import com.uade.tpo.repository.UsuarioRepository;

import java.util.*;
import java.util.stream.Collectors;

public class EmparejarPorNivel implements EmparejamientoStrategy {

    private final UsuarioDeporteRepository usuarioDeporteRepository;

    public EmparejarPorNivel(PartidoRepository partidoRepository, UsuarioDeporteRepository usuarioDeporteRepository,
            UsuarioRepository usuarioRepository) {
        this.usuarioDeporteRepository = usuarioDeporteRepository;

    }

    @Override
    public void emparejar(Partido partido) {
        Deporte deporte = partido.getDeporte();
        NivelJuego nivelMinimo = partido.getNivelMinimo();
        NivelJuego nivelMaximo = partido.getNivelMaximo();
        int cantidadJugadores = partido.getCantidadJugadores();
        Usuario organizador = partido.getOrganizador();

        List<UsuarioDeporte> usuariosDeporte = usuarioDeporteRepository.findByDeporte(deporte);

        List<Usuario> jugadoresElegibles = usuariosDeporte.stream()
                .filter(ud -> {
                    NivelJuego nivelJugador = ud.getNivelDeJuego();
                    return nivelJugador.ordinal() >= nivelMinimo.ordinal() &&
                            nivelJugador.ordinal() <= nivelMaximo.ordinal();
                })
                .map(UsuarioDeporte::getUsuario)
                .collect(Collectors.toList());

        Set<Usuario> jugadoresYaAsignados = new HashSet<>();

        if (partido.getEquipos() != null && !partido.getEquipos().isEmpty()) {
            for (Equipo equipo : partido.getEquipos()) {
                if (equipo.getJugadores() != null) {
                    jugadoresYaAsignados.addAll(equipo.getJugadores());
                }
            }
        }

        jugadoresElegibles = jugadoresElegibles.stream()
                .filter(jugador -> !jugadoresYaAsignados.contains(jugador))
                .collect(Collectors.toList());

        if (organizador != null && !jugadoresYaAsignados.contains(organizador)) {
            jugadoresElegibles.add(organizador);
        }

        if (jugadoresElegibles.isEmpty()) {
            return;
        }

        Collections.shuffle(jugadoresElegibles);

        int jugadoresPorEquipo = cantidadJugadores / 2;
        int cantidadEquipos = 2;

        List<Equipo> equipos = partido.getEquipos() != null ? new ArrayList<>(partido.getEquipos()) : new ArrayList<>();

        if (equipos.isEmpty()) {
            for (int i = 0; i < cantidadEquipos; i++) {
                Equipo equipo = new Equipo();
                equipo.setNombre("Equipo " + (i + 1));
                equipo.setJugadores(new ArrayList<>());
                equipos.add(equipo);
            }
        }

        int jugadorIndex = 0;

        for (Equipo equipo : equipos) {
            if (jugadorIndex >= jugadoresElegibles.size()) {
                break;
            }

            List<Usuario> jugadoresEquipo = equipo.getJugadores();
            if (jugadoresEquipo == null) {
                jugadoresEquipo = new ArrayList<>();
                equipo.setJugadores(jugadoresEquipo);
            }

            int jugadoresNecesarios = jugadoresPorEquipo - jugadoresEquipo.size();

            for (int i = 0; i < jugadoresNecesarios && jugadorIndex < jugadoresElegibles.size(); i++) {
                Usuario jugador = jugadoresElegibles.get(jugadorIndex);

                if (organizador != null && jugador.equals(organizador) && equipos.indexOf(equipo) == 0) {
                    jugadoresEquipo.add(jugador);
                    jugadorIndex++;
                } else if (organizador == null || !jugador.equals(organizador)) {
                    jugadoresEquipo.add(jugador);
                    jugadorIndex++;
                } else {
                    jugadorIndex++;
                    i--;
                }
            }
        }

        partido.setEquipos(equipos);
    }
}
