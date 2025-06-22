package com.uade.tpo.dto;

import lombok.Data;

@Data
public class ComentarioCreateDTO {
  private String mensaje;
  private Long jugadorId;
  private Long partidoId;
}