package com.uade.tpo.model.state;

import com.uade.tpo.model.Partido;

public class NecesitamosJugadores implements EstadoPartido {

  @Override
  public void armar(Partido contexto) {
    if (contexto.getEquipos() != null && contexto.getEquipos().size() >= 2) {
      contexto.setEstado(new PartidoArmado());
    } else {
      throw new IllegalStateException(
          "No hay suficientes equipos para armar el partido. Se necesitan al menos 2 equipos.");
    }
  }

  @Override
  public void crear(Partido contexto) {
    if (contexto.getEquipos() != null) {
      contexto.getEquipos().clear();
    }
    contexto.setEstado(new NecesitamosJugadores());
  }

  @Override
  public void cancelar(Partido contexto) {
    contexto.setEstado(new Cancelado());
  }
}