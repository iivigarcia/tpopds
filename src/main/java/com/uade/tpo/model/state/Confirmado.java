package com.uade.tpo.model.state;

import com.uade.tpo.model.Partido;

public class Confirmado implements EstadoPartido {

  @Override
  public void comenzar(Partido contexto) {
    contexto.setEstado(new EnJuego());
  }

  @Override
  public void cancelar(Partido contexto) {
    contexto.setEstado(new Cancelado());
  }
}