package com.uade.tpo.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ComentarioDTO {
  private Long id;
  private String mensaje;
  private Date fecha;
  private UsuarioDTO jugador;
  private Long partidoId;
}