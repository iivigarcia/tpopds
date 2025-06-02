package com.uade.tpo.controller;

import com.uade.tpo.model.Estadistica;
import com.uade.tpo.service.EstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaController {

    @Autowired
    private EstadisticaService estadisticaService;

    @GetMapping
    public List<Estadistica> findAll() {
        return estadisticaService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Estadistica> findById(@PathVariable Long id) {
        return estadisticaService.findById(id);
    }

    @PostMapping
    public Estadistica create(@RequestBody Estadistica estadistica) {
        return estadisticaService.save(estadistica);
    }

    @PutMapping("/{id}")
    public Estadistica update(@PathVariable Long id, @RequestBody Estadistica estadistica) {
        estadistica.setId(id);
        return estadisticaService.save(estadistica);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        estadisticaService.delete(id);
    }
}
