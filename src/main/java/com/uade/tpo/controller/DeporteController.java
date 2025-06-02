package com.uade.tpo.controller;

import com.uade.tpo.model.Deporte;
import com.uade.tpo.service.DeporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/deportes")
public class DeporteController {

    @Autowired
    private DeporteService deporteService;

    @GetMapping
    public List<Deporte> findAll() {
        return deporteService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Deporte> findById(@PathVariable Long id) {
        return deporteService.findById(id);
    }

    @PostMapping
    public Deporte create(@RequestBody Deporte deporte) {
        return deporteService.save(deporte);
    }

    @PutMapping("/{id}")
    public Deporte update(@PathVariable Long id, @RequestBody Deporte deporte) {
        deporte.setId(id);
        return deporteService.save(deporte);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        deporteService.delete(id);
    }
}
