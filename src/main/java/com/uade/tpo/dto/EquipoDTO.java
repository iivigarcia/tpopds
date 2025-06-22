package com.uade.tpo.dto;

import lombok.Data;
import java.util.List;

@Data
public class EquipoDTO {
  private Long id;
  private String nombre;
  private List<UsuarioDTO> jugadores;
}