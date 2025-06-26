package com.uade.tpo.dto;

import lombok.Data;

@Data
public class RegistroDTO {
  private String username;
  private String email;
  private String password;
  private Integer ubicacionId;
}