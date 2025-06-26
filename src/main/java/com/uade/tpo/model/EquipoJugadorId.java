package com.uade.tpo.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class EquipoJugadorId implements Serializable {

  private Long equipoId;
  private Long usuarioId;
}