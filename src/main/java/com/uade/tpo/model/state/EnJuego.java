package com.uade.tpo.model.state;

import com.uade.tpo.model.Partido;

public class EnJuego implements EstadoPartido {

  @Override
  public void finalizar(Partido contexto) {
    contexto.setEstado(new Finalizado());
  }
}