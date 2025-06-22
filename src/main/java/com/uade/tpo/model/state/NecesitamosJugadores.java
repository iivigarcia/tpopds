package com.uade.tpo.model.state;

import com.uade.tpo.model.Partido;

public class NecesitamosJugadores implements EstadoPartido {

  @Override
  public void armar(Partido contexto) {
    if (contexto.getJugadores().size() == contexto.getCantidadJugadores()) {
      contexto.setEstado(new PartidoArmado());
    } else {
      throw new IllegalStateException("No hay suficientes jugadores para armar el partido.");
    }
  }

  @Override
  public void crear(Partido contexto) {
    // Esta acción reinicia el partido, vaciando la lista de jugadores.
    contexto.getJugadores().clear();
    contexto.setEstado(new NecesitamosJugadores());
  }

  @Override
  public void cancelar(Partido contexto) {
    contexto.setEstado(new Cancelado());
  }
}