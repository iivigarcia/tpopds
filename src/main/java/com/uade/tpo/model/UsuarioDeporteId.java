package com.uade.tpo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDeporteId implements Serializable {
  private Long usuario;
  private Long deporte;
}