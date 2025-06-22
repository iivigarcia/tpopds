package com.uade.tpo.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
  private Long id;
  private String username;
  private String email;
  // No incluimos password por seguridad
}