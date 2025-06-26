package com.uade.tpo.dto;

import com.uade.tpo.model.Geolocalization;

import lombok.Data;

@Data
public class UsuarioDTO {
  private Long id;
  private String username;
  private String email;
  private Geolocalization ubicacion;
}