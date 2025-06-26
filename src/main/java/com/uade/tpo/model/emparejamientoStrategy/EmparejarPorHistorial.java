package com.uade.tpo.model.emparejamientoStrategy;

import com.uade.tpo.model.*;
import com.uade.tpo.model.state.Finalizado;
import com.uade.tpo.repository.PartidoRepository;

import java.util.*;
import java.util.stream.Collectors;

public class EmparejarPorHistorial implements EmparejamientoStrategy {

    private final PartidoRepository partidoRepository;

    public EmparejarPorHistorial(PartidoRepository partidoRepository) {
        this.partidoRepository = partidoRepository;
    }

    @Override
    public void emparejar(Partido partido) {
    }

}
