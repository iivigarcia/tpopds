package com.uade.tpo.model.emparejamientoStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.uade.tpo.model.Deporte;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.NivelJuego;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.model.UsuarioDeporte;
import com.uade.tpo.repository.EquipoRepository;
import com.uade.tpo.repository.UsuarioDeporteRepository;

public class EmparejarPorNivel implements EmparejamientoStrategy {

    private UsuarioDeporteRepository usuarioDeporteRepository;
    private EquipoRepository equipoRepository;

    public EmparejarPorNivel() {
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

        // Remove already assigned players and the organizer from eligible players
        jugadoresElegibles = jugadoresElegibles.stream()
                .filter(jugador -> !jugadoresYaAsignados.contains(jugador) && !jugador.equals(organizador))
                .collect(Collectors.toList());

        if (jugadoresElegibles.isEmpty() && organizador == null) {
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
                equipo = equipoRepository.save(equipo);
                equipos.add(equipo);
            }
        }

        // Always add organizer to the first team if not already assigned
        if (organizador != null && !jugadoresYaAsignados.contains(organizador)) {
            Equipo primerEquipo = equipos.get(0);
            List<Usuario> jugadoresPrimerEquipo = primerEquipo.getJugadores();
            if (jugadoresPrimerEquipo == null) {
                jugadoresPrimerEquipo = new ArrayList<>();
                primerEquipo.setJugadores(jugadoresPrimerEquipo);
            }
            jugadoresPrimerEquipo.add(organizador);
            equipoRepository.save(primerEquipo);
        }

        // Distribute remaining players
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
                jugadoresEquipo.add(jugador);
                jugadorIndex++;
            }

            equipoRepository.save(equipo);
        }

        partido.setEquipos(equipos);
    }

    public void setUsuarioDeporteRepository(UsuarioDeporteRepository usuarioDeporteRepository) {
        this.usuarioDeporteRepository = usuarioDeporteRepository;
    }

    public void setEquipoRepository(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }
}
