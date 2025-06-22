package com.uade.tpo.model.state;

import com.uade.tpo.model.Partido;

public interface EstadoPartido {

  default void crear(Partido contexto) {
    throw new IllegalStateException("Operación 'crear' no permitida en este estado.");
  }

  default void armar(Partido contexto) {
    throw new IllegalStateException("Operación 'armar' no permitida en este estado.");
  }

  default void confirmar(Partido contexto) {
    throw new IllegalStateException("Operación 'confirmar' no permitida en este estado.");
  }

  default void comenzar(Partido contexto) {
    throw new IllegalStateException("Operación 'comenzar' no permitida en este estado.");
  }

  default void finalizar(Partido contexto) {
    throw new IllegalStateException("Operación 'finalizar' no permitida en este estado.");
  }

  default void cancelar(Partido contexto) {
    throw new IllegalStateException("Operación 'cancelar' no permitida en este estado.");
  }
}