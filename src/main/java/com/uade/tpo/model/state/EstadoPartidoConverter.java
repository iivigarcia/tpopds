package com.uade.tpo.model.state;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoPartidoConverter implements AttributeConverter<EstadoPartido, String> {

  @Override
  public String convertToDatabaseColumn(EstadoPartido estado) {
    return estado != null ? estado.getClass().getName() : null;
  }

  @Override
  public EstadoPartido convertToEntityAttribute(String dbData) {
    if (dbData == null)
      return null;
    try {
      Class<?> clazz = Class.forName(dbData);
      return (EstadoPartido) clazz.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Error converting state from database: " + dbData, e);
    }
  }
}