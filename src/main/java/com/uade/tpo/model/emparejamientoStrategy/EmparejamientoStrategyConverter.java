package com.uade.tpo.model.emparejamientoStrategy;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmparejamientoStrategyConverter implements AttributeConverter<EmparejamientoStrategy, String> {

  @Override
  public String convertToDatabaseColumn(EmparejamientoStrategy strategy) {
    if (strategy == null) {
      return null;
    }
    return strategy.getClass().getName();
  }

  @Override
  public EmparejamientoStrategy convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }

    try {
      Class<?> clazz = Class.forName(dbData);
      return (EmparejamientoStrategy) clazz.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Error converting strategy from database: " + dbData, e);
    }
  }
}