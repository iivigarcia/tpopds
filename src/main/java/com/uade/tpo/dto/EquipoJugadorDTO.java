package com.uade.tpo.dto;

import lombok.Data;

@Data
public class EquipoJugadorDTO {
  private Long equipoId;
  private Long usuarioId;
  private UsuarioDTO usuario;
  private EquipoDTO equipo;
  private boolean inscrito;
  private boolean confirmado;
}