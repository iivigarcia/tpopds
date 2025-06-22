package com.uade.tpo.dto;

import lombok.Data;

@Data
public class ComentarioDTO {
  private Long id;
  private String contenido;
  private UsuarioDTO usuario;
  private Long partidoId; // Solo el ID del partido para evitar referencias circulares
}