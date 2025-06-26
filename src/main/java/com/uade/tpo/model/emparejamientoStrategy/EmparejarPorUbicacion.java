package com.uade.tpo.model.emparejamientoStrategy;

import com.uade.tpo.model.*;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioDeporteRepository;
import com.uade.tpo.repository.UsuarioRepository;
import com.uade.tpo.repository.EquipoRepository;

import java.util.*;
import java.util.stream.Collectors;

public class EmparejarPorUbicacion implements EmparejamientoStrategy {

    private UsuarioDeporteRepository usuarioDeporteRepository;
    private EquipoRepository equipoRepository;

    public EmparejarPorUbicacion() {
    }

    @Override
    public void emparejar(Partido partido) {
        Deporte deporte = partido.getDeporte();
        int cantidadJugadores = partido.getCantidadJugadores();
        Usuario organizador = partido.getOrganizador();
        Integer ubicacionPartido = partido.getGeolocalizationId();

        List<UsuarioDeporte> usuariosDeporte = usuarioDeporteRepository.findByDeporte(deporte);

        List<Usuario> jugadoresElegibles = usuariosDeporte.stream()
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

                if (jugador.getGeolocalizationId().equals(ubicacionPartido) || jugador.equals(organizador)) {
                    jugadoresEquipo.add(jugador);
                    jugadorIndex++;
                } else {
                    jugadorIndex++;
                    i--;
                }
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
