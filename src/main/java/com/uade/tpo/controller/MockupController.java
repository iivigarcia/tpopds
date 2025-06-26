package com.uade.tpo.controller;

import com.uade.tpo.service.MockupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mockup")
public class MockupController {

  @Autowired
  private MockupService mockupService;

  @GetMapping("/inicializar")
  public ResponseEntity<String> inicializarDB() {
    try {
      mockupService.inicializarDB();
      return ResponseEntity.ok("Base de datos inicializada exitosamente con datos de prueba");
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("Error al inicializar la base de datos: " + e.getMessage());
    }
  }

  @GetMapping("/limpiar")
  public ResponseEntity<String> limpiarDB() {
    try {
      mockupService.limpiarDB();
      return ResponseEntity.ok("Base de datos limpiada exitosamente");
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("Error al limpiar la base de datos: " + e.getMessage());
    }
  }
}