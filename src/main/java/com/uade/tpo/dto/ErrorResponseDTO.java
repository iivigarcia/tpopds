package com.uade.tpo.dto;

import lombok.Data;

@Data
public class ErrorResponseDTO {
  private String error;
  private String message;

  public ErrorResponseDTO(String error, String message) {
    this.error = error;
    this.message = message;
  }
}