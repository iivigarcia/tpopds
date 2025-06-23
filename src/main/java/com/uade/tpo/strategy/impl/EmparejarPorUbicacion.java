package com.uade.tpo.strategy.impl;

import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.strategy.EmparejamientoResultado;
import com.uade.tpo.strategy.EmparejamientoStrategy;

import java.util.*;
import java.util.stream.Collectors;

public class EmparejarPorUbicacion implements EmparejamientoStrategy {

    @Override
    public List<EmparejamientoResultado> emparejar(List<Usuario> usuarios) {
        // Agrupar usuarios por ubicación
        Map<String, List<Usuario>> usuariosPorUbicacion = usuarios.stream()
                .filter(u -> u.getUbicacion() != null)
                .collect(Collectors.groupingBy(Usuario::getUbicacion));

        List<EmparejamientoResultado> emparejamientos = new ArrayList<>();

        for (Map.Entry<String, List<Usuario>> entry : usuariosPorUbicacion.entrySet()) {
            List<Usuario> grupo = entry.getValue();

            // Agrupar en pares
            for (int i = 0; i + 1 < grupo.size(); i += 2) {
                Usuario u1 = grupo.get(i);
                Usuario u2 = grupo.get(i + 1);

                Equipo equipoLocal = new Equipo();
                equipoLocal.setJugadores(List.of(u1));

                Equipo equipoVisitante = new Equipo();
                equipoVisitante.setJugadores(List.of(u2));

                emparejamientos.add(new EmparejamientoResultado(equipoLocal, equipoVisitante));
            }
        }

        return emparejamientos;
    }
}
