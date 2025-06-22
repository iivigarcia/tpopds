package com.uade.tpo.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
  private Long id;
  private String nombre;
  private String email;
  // No incluimos password por seguridad
}