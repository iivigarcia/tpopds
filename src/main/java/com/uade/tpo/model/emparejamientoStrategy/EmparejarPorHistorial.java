package com.uade.tpo.model.emparejamientoStrategy;

import java.util.*;
import java.util.stream.Collectors;

import com.uade.tpo.model.*;
import com.uade.tpo.repository.*;

public class EmparejarPorHistorial implements EmparejamientoStrategy {

    private UsuarioDeporteRepository usuarioDeporteRepository;
    private EquipoRepository equipoRepository;
    private PartidoRepository partidoRepository;
    private EquipoJugadorRepository equipoJugadorRepository;

    public EmparejarPorHistorial() {
    }

    @Override
    public void emparejar(Partido partido) {
        Deporte deporte = partido.getDeporte();
        NivelJuego nivelMinimo = partido.getNivelMinimo();
        NivelJuego nivelMaximo = partido.getNivelMaximo();
        int cantidadJugadores = partido.getCantidadJugadores();
        Usuario organizador = partido.getOrganizador();

        List<UsuarioDeporte> usuariosDeporte = usuarioDeporteRepository.findByDeporte(deporte);
        List<Usuario> todosLosJugadores = usuariosDeporte.stream()
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

        todosLosJugadores = todosLosJugadores.stream()
                .filter(jugador -> !jugadoresYaAsignados.contains(jugador) && !jugador.equals(organizador))
                .collect(Collectors.toList());

        if (todosLosJugadores.isEmpty() && organizador == null) {
            return;
        }

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

        Map<Set<Usuario>, Integer> historialJugadores = obtenerHistorialJugadores(todosLosJugadores, deporte);

        List<Set<Usuario>> gruposHistorial = historialJugadores.entrySet().stream()
                .sorted(Map.Entry.<Set<Usuario>, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Set<Usuario> jugadoresAsignados = new HashSet<>();
        for (Set<Usuario> grupo : gruposHistorial) {
            if (jugadoresAsignados.size() >= todosLosJugadores.size())
                break;

            Equipo equipoMenosJugadores = equipos.stream()
                    .min(Comparator.comparing(e -> e.getJugadores() != null ? e.getJugadores().size() : 0))
                    .orElse(equipos.get(0));

            List<Usuario> jugadoresEquipo = equipoMenosJugadores.getJugadores();
            if (jugadoresEquipo == null) {
                jugadoresEquipo = new ArrayList<>();
                equipoMenosJugadores.setJugadores(jugadoresEquipo);
            }

            for (Usuario jugador : grupo) {
                if (!jugadoresAsignados.contains(jugador) && jugadoresEquipo.size() < jugadoresPorEquipo) {
                    jugadoresEquipo.add(jugador);
                    jugadoresAsignados.add(jugador);
                }
            }

            equipoRepository.save(equipoMenosJugadores);
        }

        boolean equiposCompletos = equipos.stream()
                .allMatch(
                        equipo -> equipo.getJugadores() != null && equipo.getJugadores().size() >= jugadoresPorEquipo);

        if (!equiposCompletos) {
            List<Usuario> jugadoresRestantes = todosLosJugadores.stream()
                    .filter(j -> !jugadoresAsignados.contains(j))
                    .filter(j -> {
                        UsuarioDeporte ud = usuariosDeporte.stream()
                                .filter(ud2 -> ud2.getUsuario().equals(j))
                                .findFirst().orElse(null);
                        if (ud == null)
                            return false;
                        NivelJuego nivelJugador = ud.getNivelDeJuego();
                        return nivelJugador.ordinal() >= nivelMinimo.ordinal() &&
                                nivelJugador.ordinal() <= nivelMaximo.ordinal();
                    })
                    .collect(Collectors.toList());

            jugadoresRestantes.sort((u1, u2) -> {
                UsuarioDeporte ud1 = usuariosDeporte.stream().filter(ud -> ud.getUsuario().equals(u1)).findFirst()
                        .orElse(null);
                UsuarioDeporte ud2 = usuariosDeporte.stream().filter(ud -> ud.getUsuario().equals(u2)).findFirst()
                        .orElse(null);
                if (ud1 == null || ud2 == null)
                    return 0;
                return Integer.compare(ud2.getNivelDeJuego().ordinal(), ud1.getNivelDeJuego().ordinal());
            });

            int jugadorIndexNivel = 0;
            for (Equipo equipo : equipos) {
                if (jugadorIndexNivel >= jugadoresRestantes.size())
                    break;

                List<Usuario> jugadoresEquipo = equipo.getJugadores();
                if (jugadoresEquipo == null) {
                    jugadoresEquipo = new ArrayList<>();
                    equipo.setJugadores(jugadoresEquipo);
                }

                int jugadoresNecesarios = jugadoresPorEquipo - jugadoresEquipo.size();

                for (int i = 0; i < jugadoresNecesarios && jugadorIndexNivel < jugadoresRestantes.size(); i++) {
                    Usuario jugador = jugadoresRestantes.get(jugadorIndexNivel);
                    jugadoresEquipo.add(jugador);
                    jugadoresAsignados.add(jugador);
                    jugadorIndexNivel++;
                }

                equipoRepository.save(equipo);
            }

            equiposCompletos = equipos.stream()
                    .allMatch(equipo -> equipo.getJugadores() != null
                            && equipo.getJugadores().size() >= jugadoresPorEquipo);
        }

        if (!equiposCompletos) {
            List<Usuario> jugadoresFinales = todosLosJugadores.stream()
                    .filter(j -> !jugadoresAsignados.contains(j))
                    .collect(Collectors.toList());

            if (!jugadoresFinales.isEmpty()) {
                jugadoresFinales.sort(Comparator.comparing(Usuario::getGeolocalizationId));

                int jugadorIndexUbicacion = 0;
                for (Equipo equipo : equipos) {
                    if (jugadorIndexUbicacion >= jugadoresFinales.size())
                        break;

                    List<Usuario> jugadoresEquipo = equipo.getJugadores();
                    if (jugadoresEquipo == null) {
                        jugadoresEquipo = new ArrayList<>();
                        equipo.setJugadores(jugadoresEquipo);
                    }

                    int jugadoresNecesarios = jugadoresPorEquipo - jugadoresEquipo.size();

                    for (int i = 0; i < jugadoresNecesarios && jugadorIndexUbicacion < jugadoresFinales.size(); i++) {
                        Usuario jugador = jugadoresFinales.get(jugadorIndexUbicacion);
                        jugadoresEquipo.add(jugador);
                        jugadorIndexUbicacion++;
                    }

                    equipoRepository.save(equipo);
                }
            }
        }

        partido.setEquipos(equipos);
    }

    private Map<Set<Usuario>, Integer> obtenerHistorialJugadores(List<Usuario> jugadores, Deporte deporte) {
        Map<Set<Usuario>, Integer> historial = new HashMap<>();

        List<Partido> partidosFinalizados = partidoRepository.findAll().stream()
                .filter(p -> p.getDeporte().equals(deporte)
                        && p.getEstado().getClass().getSimpleName().equals("Finalizado"))
                .collect(Collectors.toList());

        for (Partido partido : partidosFinalizados) {
            if (partido.getEquipos() == null)
                continue;

            for (Equipo equipo : partido.getEquipos()) {
                if (equipo.getJugadores() == null)
                    continue;

                List<Usuario> jugadoresEquipo = equipo.getJugadores().stream()
                        .filter(jugadores::contains)
                        .collect(Collectors.toList());

                if (jugadoresEquipo.size() >= 2) {
                    for (int i = 0; i < jugadoresEquipo.size() - 1; i++) {
                        for (int j = i + 1; j < jugadoresEquipo.size(); j++) {
                            Set<Usuario> par = new HashSet<>(
                                    Arrays.asList(jugadoresEquipo.get(i), jugadoresEquipo.get(j)));
                            historial.put(par, historial.getOrDefault(par, 0) + 1);
                        }
                    }
                }
            }
        }

        return historial;
    }

    public void setUsuarioDeporteRepository(UsuarioDeporteRepository usuarioDeporteRepository) {
        this.usuarioDeporteRepository = usuarioDeporteRepository;
    }

    public void setEquipoRepository(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    public void setPartidoRepository(PartidoRepository partidoRepository) {
        this.partidoRepository = partidoRepository;
    }

    public void setEquipoJugadorRepository(EquipoJugadorRepository equipoJugadorRepository) {
        this.equipoJugadorRepository = equipoJugadorRepository;
    }
}
