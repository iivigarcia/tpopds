package com.uade.tpo.strategy.impl;

import com.uade.tpo.model.*;
import com.uade.tpo.model.state.Finalizado;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.strategy.EmparejamientoResultado;
import com.uade.tpo.strategy.EmparejamientoStrategy;

import java.util.*;
import java.util.stream.Collectors;

public class EmparejarPorHistorial implements EmparejamientoStrategy {

    private final PartidoRepository partidoRepository;

    public EmparejarPorHistorial(PartidoRepository partidoRepository) {
        this.partidoRepository = partidoRepository;
    }

    @Override
    public List<EmparejamientoResultado> emparejar(List<Usuario> usuarios) {
        Map<Long, Integer> usuarioVictorias = new HashMap<>();

        // Inicializar contador
        for (Usuario usuario : usuarios) {
            usuarioVictorias.put(usuario.getId(), 0);
        }

        // Procesar partidos finalizados
        List<Partido> partidos = partidoRepository.findAll();

        for (Partido partido : partidos) {
            boolean estaFinalizado = partido.getEstado() instanceof Finalizado;
            Equipo ganador = partido.getGanador();

            if (estaFinalizado && ganador != null) {
                for (Usuario jugador : ganador.getJugadores()) {
                    if (usuarioVictorias.containsKey(jugador.getId())) {
                        usuarioVictorias.put(jugador.getId(), usuarioVictorias.get(jugador.getId()) + 1);
                    }
                }
            }
        }

        // Ordenar usuarios por cantidad de victorias (desc)
        List<Usuario> ordenados = new ArrayList<>(usuarios);
        ordenados.sort((u1, u2) -> usuarioVictorias.get(u2.getId()) - usuarioVictorias.get(u1.getId()));

        // Armar equipos por pares
        List<EmparejamientoResultado> emparejamientos = new ArrayList<>();
        for (int i = 0; i + 1 < ordenados.size(); i += 2) {
            Usuario u1 = ordenados.get(i);
            Usuario u2 = ordenados.get(i + 1);

            Equipo equipoLocal = new Equipo();
            equipoLocal.setJugadores(List.of(u1));

            Equipo equipoVisitante = new Equipo();
            equipoVisitante.setJugadores(List.of(u2));

            emparejamientos.add(new EmparejamientoResultado(equipoLocal, equipoVisitante));
        }

        return emparejamientos;
    }
}
