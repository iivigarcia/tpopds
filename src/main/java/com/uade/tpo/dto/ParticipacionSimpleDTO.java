package com.uade.tpo.dto;

import lombok.Data;

@Data
public class ParticipacionSimpleDTO {
  private Long equipoId;
  private Long usuarioId;
  private boolean inscrito;
  private boolean confirmado;
}