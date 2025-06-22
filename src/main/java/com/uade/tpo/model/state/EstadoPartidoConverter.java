package com.uade.tpo.model.state;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EstadoPartidoConverter implements AttributeConverter<EstadoPartido, String> {

  @Override
  public String convertToDatabaseColumn(EstadoPartido attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getClass().getSimpleName();
  }

  @Override
  public EstadoPartido convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      // Asumimos que las clases de estado están en este paquete.
      Class<?> clazz = Class.forName("com.uade.tpo.model.state." + dbData);
      return (EstadoPartido) clazz.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      // Manejar la excepción apropiadamente
      throw new IllegalArgumentException("Valor de estado desconocido: " + dbData, e);
    }
  }
}