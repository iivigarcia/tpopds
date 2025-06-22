package com.uade.tpo.strategy;

import com.uade.tpo.model.Equipo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmparejamientoResultado {
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
}
