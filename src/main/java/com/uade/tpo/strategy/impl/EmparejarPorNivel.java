package com.uade.tpo.strategy.impl;

import com.uade.tpo.model.*;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioDeporteRepository;
import com.uade.tpo.strategy.EmparejamientoResultado;
import com.uade.tpo.strategy.EmparejamientoStrategy;

import java.util.*;
import java.util.stream.Collectors;

public class EmparejarPorNivel implements EmparejamientoStrategy {

    private final PartidoRepository partidoRepository;
    private final UsuarioDeporteRepository usuarioDeporteRepository;

    public EmparejarPorNivel(PartidoRepository partidoRepository, UsuarioDeporteRepository usuarioDeporteRepository) {
        this.partidoRepository = partidoRepository;
        this.usuarioDeporteRepository = usuarioDeporteRepository;
    }

    @Override
    public List<EmparejamientoResultado> emparejar(List<Usuario> usuarios) {
        List<Partido> partidosAbiertos = partidoRepository.findAll().stream()
                .filter(p -> p.getJugadores().size() < p.getCantidadJugadores())
                .toList();

        List<EmparejamientoResultado> emparejamientos = new ArrayList<>();

        for (Partido partido : partidosAbiertos) {
            NivelJuego nivelMin = partido.getNivelMinimo();
            NivelJuego nivelMax = partido.getNivelMaximo();
            Deporte deporte = partido.getDeporte();

            // Filtrar usuarios con nivel válido para ese deporte
            List<Usuario> candidatos = usuarios.stream()
                    .filter(u -> tieneNivelValidoParaDeporte(u, deporte, nivelMin, nivelMax))
                    .toList();

            for (int i = 0; i + 1 < candidatos.size(); i += 2) {
                Usuario u1 = candidatos.get(i);
                Usuario u2 = candidatos.get(i + 1);

                // Crear equipos con los usuarios
                Equipo equipoLocal = new Equipo();
                equipoLocal.setJugadores(List.of(u1));

                Equipo equipoVisitante = new Equipo();
                equipoVisitante.setJugadores(List.of(u2));

                // Asignarlos al partido
                partido.setEquipoLocal(equipoLocal);
                partido.setEquipoVisitante(equipoVisitante);

                // También agregarlos a la lista de jugadores del partido
                partido.getJugadores().add(u1);
                partido.getJugadores().add(u2);

                emparejamientos.add(new EmparejamientoResultado(equipoLocal, equipoVisitante));

                break; // Emparejamos solo un par por partido, podés sacar este break si querés más de un emparejamiento por partido
            }

            partidoRepository.save(partido);
        }

        return emparejamientos;
    }

    private boolean tieneNivelValidoParaDeporte(Usuario usuario, Deporte deporte, NivelJuego min, NivelJuego max) {
        return usuarioDeporteRepository.findByUsuarioAndDeporte(usuario, deporte)
                .map(ud -> {
                    int nivel = getValorNivel(ud.getNivelDeJuego());
                    int minVal = (min != null) ? getValorNivel(min) : 1;
                    int maxVal = (max != null) ? getValorNivel(max) : 3;
                    return nivel >= minVal && nivel <= maxVal;
                })
                .orElse(false);
    }

    private int getValorNivel(NivelJuego nivel) {
        return switch (nivel) {
            case PRINCIPIANTE -> 1;
            case INTERMEDIO -> 2;
            case AVANZADO -> 3;
        };
    }
}
