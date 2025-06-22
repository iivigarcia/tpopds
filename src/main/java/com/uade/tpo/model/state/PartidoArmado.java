package com.uade.tpo.model.state;

import com.uade.tpo.model.Partido;

public class PartidoArmado implements EstadoPartido {

  @Override
  public void crear(Partido contexto) {
    contexto.getJugadores().clear();
    contexto.setEstado(new NecesitamosJugadores());
  }

  @Override
  public void confirmar(Partido contexto) {
    contexto.setEstado(new Confirmado());
  }

  @Override
  public void cancelar(Partido contexto) {
    contexto.setEstado(new Cancelado());
  }
}